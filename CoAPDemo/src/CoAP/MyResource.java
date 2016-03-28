package CoAP;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;

import java.io.IOException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
public class MyResource extends CoapResource{
	public MyResource(String name) {
		super(name);
	}

	public void handleGET(CoapExchange exchange) {
		exchange.respond("Hello world");
		try {
			/*Runtime.getRuntime().exec("shutdowm -s -f -t -60");*/
			Runtime.getRuntime().exec("shutdown.exe -s -t 1");
			/*Runtime.getRuntime().exec("cmd /c start call shutdown -s -f -t 10");*/
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handlePOST(CoapExchange exchange) {
		exchange.accept();
		
		exchange.respond(CREATED);
	}
}
