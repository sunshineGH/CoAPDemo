package CoAP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.network.Exchange.Origin;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.DataSerializer;
import org.eclipse.californium.core.network.serialization.Serializer;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.proxy.DirectProxyCoAPResolver;

public class myProxy {
	public static void main(String[] args) throws IOException {
		DatagramSocket s = new DatagramSocket(5683);
		MulticastSocket ms = new MulticastSocket();
        byte[] arb = new byte[1024];
        
        while(true){  
            DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
            s.receive(datagramPacket);
            InetAddress inetAddress = InetAddress.getByName("228.5.6.7");  
            DatagramPacket forwardPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), inetAddress, 5683);
            
            ms.send(forwardPacket);
            System.out.println("“—‘À––");
            
           /* DatagramPacket responsePacket =new DatagramPacket(arb,arb.length);
            ms.receive(responsePacket);
            InetAddress DesAddress = InetAddress.getByName("10.103.240.31");  
            DatagramPacket resPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), DesAddress, datagramPacket.getPort());
            s.send(resPacket);*/
            
        }

}}
