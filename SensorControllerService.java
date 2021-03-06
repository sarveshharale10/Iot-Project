import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

class SensorController implements Runnable{
	GpioController gpio;
	GpioPinDigitalInput input;

	HashSet<String> sendEvents;
	BlockingQueue<String> sendQueue;

	SensorController(BlockingQueue<String> sendQueue,String fileName){
		this.sendQueue = sendQueue;

		sendEvents = new HashSet<String>();
		try{
			BufferedReader inFromFile = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = inFromFile.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				String type = st.nextToken();
				if(type.compareTo("SEND") == 0){
					sendEvents.add(st.nextToken());
				}
				
			}
			gpio = GpioFactory.getInstance();
			input = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);
			if(sendEvents.contains("MOTIONDETECTED")) {
	
		        // create and register gpio pin listener
		        input.addListener(new GpioPinListenerDigital() {
		                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		                    if(event.getState().isHigh()){
		                    	try{
		                    		sendQueue.put("MOTIONDETECTED");
		                    	}catch(Exception e){}
		                    }
		                }
		            });
			}
		}catch(Exception e){}
	}

	public void run(){
		while(true){}
	}

}

class SensorControllerService{
	public static void main(String[] args) throws Exception {
		BlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(10);

		Thread controllerService = new Thread(new SensorController(sendQueue,args[0]));
		Thread eventSender = new Thread(new EventSender(sendQueue));
		controllerService.start();
		eventSender.start();
	}
}