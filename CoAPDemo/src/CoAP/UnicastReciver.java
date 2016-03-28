package CoAP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.serialization.DataParser;

public class UnicastReciver {
	public static void main(String[] args) throws IOException {
		DatagramSocket s = new DatagramSocket(5683);
        byte[] arb = new byte[1024];
        
        while(true){  
            DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
            s.receive(datagramPacket);
            DataParser dataParser = new DataParser(arb);
            Request request = dataParser.parseRequest();
            System.out.println(datagramPacket.getPort());
            System.out.println(request.getCode());
            System.out.println(request.getURI());
            System.out.println("reciver multicasting: " + request.isMulticast());      
            System.out.println(request.getPayloadString());
	}
  }
}
