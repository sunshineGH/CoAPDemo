package CoAP;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.Serializer;
import org.eclipse.californium.elements.RawData;

public class MyProxyEntity2 implements Serializable{
	static int count = 0;
	public static void main(String[] args) {
		//创建一个Entity
		try {
			
		InetAddress group = InetAddress.getByName("228.5.6.7"); //Group
		InetAddress[] nodes = null; //单播nodes
		//nodes[0] = InetAddress.getByName("10.103.241.142");
		//des[1] = InetAddress.getByName("10.103.241.137");
		Entity entity1 = new Entity(group,nodes,"Entity1");
				
		//创建一个接收报文
		DatagramSocket s = new DatagramSocket(5683);
		MulticastSocket ms = new MulticastSocket();
        byte[] arb = new byte[1024];
        byte[] arb1 = new byte[1024];
        boolean flag = true;
        
        while(true){ 
        	DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
            s.receive(datagramPacket);
            DataParser dataParser = new DataParser(arb);
            Request request = dataParser.parseRequest();
        	//接收register	   
            System.out.println(count++);
            //System.out.println(request.getOptions().getUriPathString());
            if(request.getOptions().getUriPathString() == "") {
            String URIQuery = request.getOptions().getUriQueryString();
            System.out.println(URIQuery);
            if(URIQuery.substring(0, 8).equals("register")) {
            	/*entity1.groupNum++;
            	System.out.println("dhahda");
            	continue;*/
            	//entity1.groupNodes[entity1.groupNum] = request.getSource(); 
            	//entity1.groupNodes.add(request.getSource());
            	entity1.groupNodes2.put(request.getSource(), false);
            	entity1.groupNum++;
            	//回复ACK
            	Response ACKResponse = Response.createResponse(request, ResponseCode.CREATED);
            	ACKResponse.setType(Type.ACK);

            	System.out.println(ACKResponse);
            	Serializer serializer1 = new Serializer();
            	
                RawData sendACKResponse = serializer1.serialize(ACKResponse); 
                System.out.println(datagramPacket.getAddress());
                DatagramPacket ACKPacket = new DatagramPacket(sendACKResponse.getBytes(), sendACKResponse.getSize(), datagramPacket.getAddress(), datagramPacket.getPort());
             	s.send(ACKPacket);
            	continue ;

            }
            }
        	//转发request
            //DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
            //s.receive(datagramPacket);
            //DataParser dataParser = new DataParser(arb);
            //Request request = dataParser.parseRequest();
            String entityURI = request.getOptions().getUriPathString();
            if(entityURI.equals(entity1.getName())) {
            	InetAddress multiAddress = entity1.group; 
            	DatagramPacket forwardPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), multiAddress, 5683);
            	ms.send(forwardPacket);
            	if(nodes != null){
            	for(int i=0;i<entity1.nodes.length;i++) {
            		InetAddress nodeAddress = entity1.nodes[i];
            		DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
            		System.out.println(unicastPacket);
            		s.send(unicastPacket);
            	}
            	}
            }   
            
            System.out.println("已运行");
            
            int sum = 0; //要来记录目前已经接收到的response的个数
            //接收response
            for(int i = 0;i < entity1.groupNum;i++) {
            	System.out.println("进入response" + i);
            	try{
            		s.setSoTimeout(1000);
            		System.out.println("=============");
            		DatagramPacket responsePacket =new DatagramPacket(arb1,arb1.length);
                    s.receive(responsePacket);
                    System.out.println("&&&&&&&&&&&&");
                    if(entity1.groupNodes2.get(responsePacket.getAddress())==false) {
                    	entity1.groupNodes2.put(responsePacket.getAddress(), true);
                    	sum++;
                    }                    	
                }catch(Exception e){
                	//e.printStackTrace();
                	if(sum < entity1.groupNum/2 ) { //多播重发
                		InetAddress multiAddress = entity1.group; 
                    	DatagramPacket forwardPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), multiAddress, 5683);
                    	ms.send(forwardPacket);
                    	i = 0;
                		continue;
                	}else {   //单播重发
                		for(Entry<InetAddress, Boolean> entity4 : entity1.groupNodes2.entrySet()){
                        	//System.out.println("**************************");
                        	if(entity4.getValue() == false) {
                        		InetAddress nodeAddress = entity4.getKey();
                        		DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
                        		System.out.println(unicastPacket);
                        		s.send(unicastPacket);
                        		entity1.groupNodes2.put(entity4.getKey(), true); //直接将节点状态改变
                        	}
                        }
                	}
                }      	
            }
            
            System.out.println("#####################"+entity1.groupNodes2.isEmpty());
            
            for(Entry<InetAddress, Boolean> entity4 : entity1.groupNodes2.entrySet()){
            	System.out.println("**************************");
            	System.out.println(entity4.getKey() + "    "  + entity4.getValue());
            }
            
            //确认收到所有的response之后，回复给sender端一个response
            /*DatagramPacket responsePacket =new DatagramPacket(arb,arb.length);
            ms.receive(responsePacket);*/
            Response finalResponse = Response.createResponse(request, ResponseCode.CHANGED);
            finalResponse.setType(Type.NON);
            finalResponse.setPayload("finalResponse");
         	Serializer serializer1 = new Serializer();
            RawData sendFinalResponse = serializer1.serialize(finalResponse); 
            InetAddress DesAddress = InetAddress.getByName("10.103.240.31");  
            DatagramPacket resPacket = new DatagramPacket(sendFinalResponse.getBytes(), sendFinalResponse.getSize(),DesAddress,5683);
            s.send(resPacket);
            //System.out.println(entity1.groupNum);
            
            //执行完之后需要将组内节点的状态全部置为false
            for(Entry<InetAddress, Boolean> entity4 : entity1.groupNodes2.entrySet()){
            	System.out.println("**************************");
            	entity1.groupNodes2.put(entity4.getKey(), false) ;
            }
        }
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
		
}
