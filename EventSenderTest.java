import java.util.concurrent.*;
import java.net.*;
import java.util.*;

class EventSender implements Runnable{
	BlockingQueue<String> eventSendQueue;
	DatagramSocket out;
	DatagramPacket outPacket;

	EventSender(BlockingQueue<String> eventSendQueue){
		this.eventSendQueue = eventSendQueue;
		try{
			out = new DatagramSocket();
		}catch(Exception e){}
	}

	public void run(){
		while(true){
			try{
				String event = String.format("%50s",eventSendQueue.take());
				outPacket = new DatagramPacket(event.getBytes(),0,50,InetAddress.getByName("255.255.255.255"),5000);
				out.send(outPacket);
			}catch(Exception e){}
		}
	}
}

class EventSenderTest{
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
		Thread sender = new Thread(new EventSender(queue));
		sender.start();
		while(true){
			try{
				String next = String.format("%50s",s.nextLine());		
				queue.put(next);
			}catch(Exception e){}
		}
	}
}