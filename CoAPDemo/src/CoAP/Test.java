package CoAP;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.serialization.DataSerializer;

public class Test {
	public static void main(String[] args) throws Exception {
		int port = 5683;      
        //String sendMessage= "Hello world";
        Request request = new Request(Code.GET,Type.NON);
        //request.setURI("228.5.6.7:5683/res");
        request.setMulticast(true);
        byte[] bToken = ("01001".getBytes());
        request.setToken(bToken);
        request.setPayload("Hello world");
        OptionSet options = new OptionSet();
        options.setUriPath("Entity1");
        options.setUriQuery("close");
        options.setProxyUri("10.103.241.142");
        request.setOptions(options);
        DataSerializer serializer = new DataSerializer();
        byte[] sendMessage = serializer.serializeRequest(request);
        InetAddress inetAddress = InetAddress.getByName("228.5.6.7");  
        InetAddress proxyAddress = InetAddress.getByName("10.103.241.137");
        /*DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, port);*/  
        DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, port);
        MulticastSocket multicastSocket = new MulticastSocket();  
        multicastSocket.send(datagramPacket);
        System.out.print("dajdjaij");
	}
}
