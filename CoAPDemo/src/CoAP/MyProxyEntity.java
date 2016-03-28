package CoAP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.Serializer;
import org.eclipse.californium.elements.RawData;

public class MyProxyEntity {
	public static void main(String[] args) throws IOException {
		//创建一个Entity
		InetAddress group = InetAddress.getByName("228.5.6.7"); //Group
		InetAddress[] nodes = new InetAddress[1]; //单播nodes
		nodes[0] = InetAddress.getByName("10.103.241.142");
		//nodes[1] = InetAddress.getByName("10.103.241.137");
		Entity entity1 = new Entity(group,nodes,"entity1");
				
		//创建一个接收报文
		DatagramSocket s = new DatagramSocket(5683);
		MulticastSocket ms = new MulticastSocket();
        byte[] arb = new byte[1024];
        
        while(true){ 
        	//flag1:
        	
        	DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
            s.receive(datagramPacket);
            DataParser dataParser = new DataParser(arb);
            Request request = dataParser.parseRequest();
        	//接收register	   
            String URIQuery = request.getOptions().getUriQueryString();
            if(URIQuery=="register") {
            	entity1.groupNodes[entity1.groupNum] = request.getSource(); 
            	entity1.groupNum++;
            	//回复ACK
            	Response ACKResponse = Response.createResponse(request, ResponseCode.CREATED);
            	ACKResponse.setType(Type.ACK);
            	Serializer serializer1 = new Serializer();
                RawData sendACKResponse = serializer1.serialize(ACKResponse); 
                DatagramPacket ACKPacket = new DatagramPacket(sendACKResponse.getBytes(), sendACKResponse.getSize(), request.getSource(), datagramPacket.getPort());
             	s.send(ACKPacket);
            	continue ;
            }
        	
        	//接收request
            //DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
            //s.receive(datagramPacket);
            //DataParser dataParser = new DataParser(arb);
            //Request request = dataParser.parseRequest();
            String entityURI = request.getOptions().getUriPathString();
            if(entityURI.equals(entity1.getName())) {
            	InetAddress multiAddress = entity1.group; 
            	DatagramPacket forwardPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), multiAddress, 5683);
            	ms.send(forwardPacket);
            	
            	for(int i=0;i<entity1.nodes.length;i++) {
            		InetAddress nodeAddress = entity1.nodes[i];
            		DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
            		s.send(unicastPacket);
            	}
            }   
            System.out.println("已运行");
            
            for(int i=0;i<entity1.groupNum;i++) {
            	DatagramPacket responsePacket =new DatagramPacket(arb,arb.length);
                s.receive(responsePacket);
                try{
                	s.setSoTimeout(2000);
                }catch(SocketException e){
                	
                }
                //entity1.groupNodes2     
            }
            
            InetAddress DesAddress = InetAddress.getByName("10.103.240.31");  
            DatagramPacket resPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), DesAddress, datagramPacket.getPort());
            s.send(resPacket);
            
        }
		
	}
}
