package CoAP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.serialization.DataSerializer;

public class RegisterRequest extends Request{
	String uri;
	public RegisterRequest(String uri) {
		super(Code.POST, Type.CON);
		super.setURI(uri);
		OptionSet options = new OptionSet();
		options.setUriQuery("register");
		super.setOptions(options);
	}
	
	public void sendRequest() throws Exception {
		int port = 5683;
		DataSerializer serializer = new DataSerializer();
        byte[] sendMessage = serializer.serializeRequest(this);
        InetAddress proxyAddress = InetAddress.getByName(this.uri);
        DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, port);
        DatagramSocket unicastSocket = new DatagramSocket();  
        unicastSocket.send(datagramPacket);  
	}
	
}
