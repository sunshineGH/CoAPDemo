package CoAP;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

public class MyClient {
	public static void main(String[] args) {
		CoapClient client = new CoapClient("10.103.241.142:5683/res");
		System.out.println(client.get().getResponseText());
	}
} 
