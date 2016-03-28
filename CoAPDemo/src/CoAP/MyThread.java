package CoAP;
import java.net.DatagramPacket;  
import java.net.InetAddress;  
import java.net.MulticastSocket;  
import java.net.UnknownHostException;  

public class MyThread extends Thread{
	int i;
	MyThread(int i) {
		super();
		this.i = i;
	}
	public void run() {
		System.out.println("MyThread" + this.i + " run");
		
	}
}
