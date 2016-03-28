package CoAP;

import java.net.DatagramPacket;  
import java.net.DatagramSocket;
import java.net.InetAddress;  
import java.net.InetSocketAddress;
import java.net.MulticastSocket;  
import java.net.SocketAddress;
import java.net.UnknownHostException;  

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.DataSerializer;
import org.eclipse.californium.core.network.serialization.Serializer;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.UDPConnector;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
  
/** 
 *  
 * �ಥ������ 
 
 */  
public class BroadCastReciever  
{  
      /** 
       * @param args 
       * @throws UnknownHostException  
       */  
      public static void main ( String [] args ) throws Exception  
      {  
    	  /*InetAddress groupAddress = InetAddress.getByName("228.5.6.7");  
    	  Group group = new Group(groupAddress);
          MulticastSocket s = new MulticastSocket(5683);
          byte[] arb = new byte[1024];  
          s.joinGroup(group.groupAddress);//�������  
          group.groupNum++;*/
          InetAddress group = InetAddress.getByName("228.5.6.7");  
          MulticastSocket ms = new MulticastSocket(5683);
          byte[] arb = new byte[1024]; 
          System.out.println("==========���������==========");
          ms.joinGroup(group);//�������  
          System.out.println("==========�ɹ�������==========");
          //ע����Ϣ
          Request register = new Request(Code.POST,Type.CON);
          register.setToken("0101001".getBytes());
          OptionSet options = new OptionSet();
  		  options.setUriQuery("register");
  		  register.setOptions(options);
  		  System.out.println(register.getOptions().getUriQueryString());
	  	  DataSerializer serializer = new DataSerializer();
	      byte[] sendMessage = serializer.serializeRequest(register);
	      InetAddress proxyAddress = InetAddress.getByName("10.103.243.115");
	      DatagramPacket registerPacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, 5683);
	      DatagramSocket unicastSocket = new DatagramSocket();
	      System.out.println();
	      System.out.println("==========����register==========");
	      unicastSocket.send(registerPacket);
	      //����ACK
	      DatagramPacket ACKPacket =new DatagramPacket(arb,arb.length); 
	      System.out.println("==========������ճɹ�ע��ACK==========");
	      unicastSocket.receive(ACKPacket);
	      System.out.println("=========������ɣ�ע��ɹ�=========");
	      //����request
          while(true){  
               DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);
               System.out.println();
               System.out.println("==========�������request==========");
               ms.receive(datagramPacket);
               System.out.println("==========����request���==========");
               DataParser dataParser = new DataParser(datagramPacket.getData());
               Request request = dataParser.parseRequest();
               System.out.println();
               System.out.println("==========request����==========");
               //System.out.println(datagramPacket.getPort());
               System.out.println("request Code : " + request.getCode());
               System.out.println("request URI  : " + request.getURI());
               //System.out.println("reciver multicasting: " + request.isMulticast());      
               System.out.println("request payload : " +request.getPayloadString());
               System.out.println("datagramPacket source IP : " + datagramPacket.getAddress());
               //System.out.println(datagramPacket.getPort());
 
               if(request.getCode().equals(Code.GET)) {
              	 Response response = Response.createResponse(request, ResponseCode.CHANGED);
              	 //System.out.println(response.getTokenString());
              	 response.setType(Type.NON);
              	 response.setPayload("response");
              	 Serializer serializer1 = new Serializer();
                 RawData sendResponse = serializer1.serialize(response); 
                 DatagramPacket responsePacket = new DatagramPacket(sendResponse.getBytes(), sendResponse.getSize(), datagramPacket.getAddress(), 5683);
                 System.out.println();
                 System.out.println("==========����response==========");
                 unicastSocket.send(responsePacket);
                 System.out.println("==========response�������==========");
               }
               //System.out.println(arb.length);  
               //System.out.println(new String(arb));     
          }        

            /*while(true){  
                 DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);       
                 s.receive(datagramPacket);
                 DataParser dataParser = new DataParser(arb);
                 Request request = dataParser.parseRequest();
                 
                 System.out.println(request.toString());
                 System.out.println(request.getDestinationPort());
                 System.out.println(request.getCode());   
                 System.out.println(request.getURI());
                 System.out.println(request.isMulticast());      
                 System.out.println(request.getPayloadString());
                 if(request.getPayloadString()=="Hello world") {
                	 Response response = Response.createResponse(request, ResponseCode.VALID);
                	 
                 }
                 System.out.println(arb.length);  
                 System.out.println(new String(arb));     
            }*/        
      }  
  
}  
