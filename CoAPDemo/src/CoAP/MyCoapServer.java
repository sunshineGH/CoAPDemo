package CoAP;

import org.eclipse.californium.core.CoapServer;

public class MyCoapServer {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CoapServer server = new CoapServer();
		MyResource r1 = new MyResource("Hello");
		server.add(r1);
		System.out.println(r1.getURI());
		server.start();
	}

}
