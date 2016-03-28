package CoAP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.DataSerializer;

public class UniCastSend {
	
	public static void main(String[] args) throws Exception {
		int port = 5683;  
	    
	    //String sendMessage= "Hello world";
	    Request request = new Request(Code.GET,Type.NON);
	    //request.setURI("228.5.6.7:5683/res");
	    //request.setMulticast(true);
	    byte[] bToken = ("01001".getBytes());
	    request.setToken(bToken);
	    request.setPayload("Hello world");
	    OptionSet options = new OptionSet();
	    options.setProxyUri("10.103.241.142");
	    options.setUriPath("entity1");
	    options.setUriQuery("close");
	    request.setOptions(options);
	    
	   /* DirectProxyCoAPResolver proxy = new DirectProxyCoAPResolver();
	    Exchange exchange = new Exchange(request,Origin.REMOTE);
	    proxy.forwardRequest(exchange);*/
    
	    DataSerializer serializer = new DataSerializer();
	    byte[] sendMessage = serializer.serializeRequest(request);
	    //System.out.println("sender multicating: " + request.isMulticast());
	    //System.out.println(request.getOptions().getUriPathString());
	    //System.out.print(request.getType());
    
	    //String sendMessage = request.toString();
	    InetAddress inetAddress = InetAddress.getByName("228.5.6.7");  
	    InetAddress proxyAddress = InetAddress.getByName("10.103.241.137");
	    /*DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, port);*/  
	    DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, port);
	    DatagramSocket unicastSocket = new DatagramSocket();  
	    unicastSocket.send(datagramPacket);  
    
	   // MulticastSocket multicastSocket = new MulticastSocket();
	   /* 
	    Response response = request.waitForResponse();
	    System.out.print(response.toString());*/
	   while(true) {
	    byte[] arb = new byte[1024]; 
	     
	    DatagramPacket responsePacket =new DatagramPacket(arb,arb.length);       
	    unicastSocket.receive(responsePacket);
	    DataParser dataParser = new DataParser(arb);
	    Response response = dataParser.parseResponse();
	    System.out.println(response.getPayloadString());
	   }
  }
}
