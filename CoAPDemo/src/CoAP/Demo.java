package CoAP;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

public class Demo {
	public static void main(String[] args) throws InterruptedException, IOException {
		Request request = new Request(Code.GET);
		request.setURI("coap://224.0.0.1:5683");
		request.setMulticast(true);
		request.send();
		//client.send(request);
		Response response = request.waitForResponse();
		System.out.println(response);
	}
}
