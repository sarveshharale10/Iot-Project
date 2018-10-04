import java.util.*;
import com.pi4j.io.gpio.*;
import java.util.concurrent.*;
import java.io.*;

class RelayController implements Runnable{
	
	HashSet<String> sendEvents;
	HashMap<String,String> recvEvents;

	BlockingQueue<String> sendQueue,recvQueue;

	RelayController(BlockingQueue<String> sendQueue,BlockingQueue<String> recvQueue,String fileName){
		this.sendQueue = sendQueue;
		this.recvQueue = recvQueue;

		sendEvents = new HashSet<String>();
		recvEvents = new HashMap<String,String>();

		try{
			BufferedReader inFromFile = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = inFromFile.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				String type = st.nextToken();
				if(type.compareTo("SEND") == 0){
					sendEvents.add(st.nextToken());
				}
				else if(type.compareTo("RECV") == 0){
					recvEvents.put(st.nextToken(),st.nextToken());
				}
			}
		}catch(Exception e){}
	}
	final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);

	void switchOn(){
		led.blink(1000);
		//System.out.println("Switched on");
		if(sendEvents.contains("ON")){
			try{
				sendQueue.put("ON");
			}catch(Exception e){}
		}
	}

	void switchOff(){
		//whatever to switch off
		led.low();
		//System.out.println("Switched off");
		if(sendEvents.contains("OFF")){
			try{
				sendQueue.put("OFF");
			}catch(Exception e){}
		}
	}

	void performAction(String actionName){
		switch(actionName){
			case "ON":
			switchOn();
			break;

			case "OFF":
			switchOff();
			break;

		}
	}

	public void run(){
		while(true){
			try{
				String event = recvQueue.take();
				String action = recvEvents.get(event);
				performAction(action);
			}catch(Exception e){}	
		}
	}

}

class RelayControllerService{
	public static void main(String[] args) {
		BlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(10);
		BlockingQueue<String> recvQueue = new ArrayBlockingQueue<String>(10);

		Thread controllerService = new Thread(new RelayController(sendQueue,recvQueue,args[0]));
		Thread eventSender = new Thread(new EventSender(sendQueue));
		Thread eventReceiver = new Thread(new EventReceiver(recvQueue));

		controllerService.start();
		eventSender.start();
		eventReceiver.start();

	}
}