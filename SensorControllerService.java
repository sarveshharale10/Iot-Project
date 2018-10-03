import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;

class SensorController implements Runnable{
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
		}catch(Exception e){}
	}


	public void run(){
		while(true){
			try{
				Thread.sleep(10);
			}catch(Exception e){}
		}
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