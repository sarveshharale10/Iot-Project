import java.net.*;
import java.util.concurrent.*;

class EventReceiver implements Runnable{
	BlockingQueue<String> eventRecvQueue;
	DatagramSocket in;
	DatagramPacket inPacket;

	EventReceiver(BlockingQueue<String> eventRecvQueue){
		this.eventRecvQueue = eventRecvQueue;
		try{
			in = new DatagramSocket(5000);
		}catch(Exception e){}
	}

	public void run(){
		while(true){
			try{
				byte[] buffer = new byte[50];
				inPacket = new DatagramPacket(buffer,buffer.length);
				in.receive(inPacket);
				String event = new String(inPacket.getData()).trim();
				eventRecvQueue.put(event);
			}catch(Exception e){}
		}
	}
}

class EventReceiverTest{
	public static void main(String[] args) throws Exception{
		BlockingQueue<String> eventRecvQueue = new ArrayBlockingQueue<String>(10);
		Thread receiver = new Thread(new EventReceiver(eventRecvQueue));
		receiver.start();
	}
}