package CoAP;

import java.io.IOException;
import java.net.DatagramPacket;  
import java.net.DatagramSocket;
import java.net.InetAddress;  
import java.net.InetSocketAddress;
import java.net.MulticastSocket;  
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;  
import java.util.Timer;
import java.util.TimerTask;

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
 * 多播接收者 
 
 */  
public class BroadCastReciever2  
{  
	     int sleepState;
	     int sleepDuration; //睡眠周期,单位都是ms
	     int timeSleeping;  //已睡眠时间
	     int nextSleep; //下一个睡眠发生的时间，对于sleepState=0的情况
	     InetAddress address;
	     DatagramSocket unicastSocket; //单播socket
	     MulticastSocket ms; //多播socket
	     boolean isRecieve;
	     
	     public BroadCastReciever2() throws Exception {
	    	 unicastSocket = new DatagramSocket();
	    	 ms = new MulticastSocket(5683);
	    	 isRecieve = false;
	     }
	     public BroadCastReciever2(InetAddress address,int sleepState,int sleepDuration,int timeSleeping,int nextSleep,boolean isRecieve) throws Exception {
	    	 this.address = address;
	    	 this.sleepState = sleepState;//val=1,表示sleep;val=0,表示awake
	    	 this.sleepDuration = sleepDuration;
	    	 this.timeSleeping = timeSleeping;
	    	 this.nextSleep = nextSleep;
	    	 this.isRecieve = false;
	     }
	     public BroadCastReciever2(int sleepState,int sleepDuration,int timeSleeping,int nextSleep) throws Exception {
	    	 unicastSocket = new DatagramSocket();
	    	 ms = new MulticastSocket(5683);
	    	 isRecieve = false;
	    	 this.sleepState = sleepState;//val=1,表示sleep;val=0,表示awake
	    	 this.sleepDuration = sleepDuration;
	    	 this.timeSleeping = timeSleeping;
	    	 this.nextSleep = nextSleep;
	     }
	     public void setSleepValue(int sleepState,int sleepDuration,int timeSleeping,int nextSleep) {
	    	 this.sleepState = sleepState;//val=1,表示sleep;val=0,表示awake
	    	 this.sleepDuration = sleepDuration;
	    	 this.timeSleeping = timeSleeping;
	    	 this.nextSleep = nextSleep;
	     }
	     public void setInetAddress(InetAddress address) {
	    	 this.address = address;
	     }
	     public void setIsRecieve(boolean isRecieve) {
	    	 this.isRecieve = isRecieve; 
	     }
	     public void setDatagramSocket(DatagramSocket unicastSocket) {
	    	 this.unicastSocket = unicastSocket;
	     }
	     public void setMulticastSocket(MulticastSocket ms) {
	    	 this.ms = ms;
	     }
	     public InetAddress getInetAddress() {
	    	 return address;
	     }
	     public boolean getIsRecieve() {
	    	 return this.isRecieve; 
	     }
	     public int getSleepState() {
	    	 return this.sleepState;
	     }
	     
	     public int getSleepDuration() {
	    	 return this.sleepDuration;
	     }
	     
	     public int getTimeSleeping() {
	    	 return this.timeSleeping;
	     }
	     
	     public int getNextSleep() {
	    	 return this.nextSleep;
	     }
	     public DatagramSocket getDatagramSocket() {
	    	 return this.unicastSocket;
	     }
	     public MulticastSocket getMulticastSocket() {
	    	 return this.ms;
	     }
	     public void sleeping() {
	    	 ms.close();
	    	 unicastSocket.close();
	     }
	     public void awaking() throws IOException {
	    	 if(ms.isClosed()==true) 
	    		 ms = new MulticastSocket(5683);
	    	 if(unicastSocket.isClosed()==true) 
	    		 unicastSocket = new DatagramSocket();
	     }
	     
	     public void joinInGroup(InetAddress group) throws IOException {
	    	  System.out.println("==========进入加入组==========");
	          ms.joinGroup(group);//加入该组  
	          System.out.println("==========成功加入组==========");
	     }
	     public void sendRegisterPacket() throws Exception {
	    	  Request register = new Request(Code.POST,Type.CON);
	          register.setToken("0101001".getBytes());
	          String payload = "sleepState: " + sleepState + " sleepDuration: " + sleepDuration +" timeSleeping: " + 
	                                timeSleeping + " nextSleep: " + nextSleep;
	          register.setPayload(payload);
	          OptionSet options = new OptionSet();
	  		  options.setUriQuery("register");
	  		  register.setOptions(options);
	  		  System.out.println(register.getOptions().getUriQueryString());
		  	  DataSerializer serializer = new DataSerializer();
		      byte[] sendMessage = serializer.serializeRequest(register);
		      InetAddress proxyAddress = InetAddress.getByName("10.103.241.137");
		      DatagramPacket registerPacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, 5683);
		      //DatagramSocket unicastSocket = new DatagramSocket();
		      System.out.println();
		      System.out.println("==========进入register==========");
		      unicastSocket.send(registerPacket);
		      System.out.println("==========register发送完成==========");
	     }
	     
	     public void recieveACK() throws Exception {
	    	  byte[] arb = new byte[1024];
	    	  //DatagramSocket unicastSocket = new DatagramSocket();
	    	  DatagramPacket ACKPacket =new DatagramPacket(arb,arb.length); 
		      System.out.println("==========进入接收成功注册ACK==========");
		      unicastSocket.receive(ACKPacket);
		      System.out.println("=========接收完成，注册成功=========");
	     }
	     
	     public DatagramPacket recieveRequest() throws Exception {
	    	 //MulticastSocket ms = new MulticastSocket(5683);
	    	 byte[] arb = new byte[1024];
	    	 DatagramPacket datagramPacket = new DatagramPacket(arb,arb.length);
             System.out.println();
             System.out.println("==========进入接收request==========");
             ms.receive(datagramPacket);
             System.out.println("==========接收request完成==========");
             DataParser dataParser = new DataParser(datagramPacket.getData());
             Request request = dataParser.parseRequest();
             System.out.println();
             System.out.println("==========request解析==========");
             //System.out.println(datagramPacket.getPort());
             System.out.println("request Code : " + request.getCode());
             System.out.println("request URI  : " + request.getURI());
             //System.out.println("reciver multicasting: " + request.isMulticast());      
             System.out.println("request payload : " +request.getPayloadString());
             System.out.println("datagramPacket source IP : " + datagramPacket.getAddress());
             //System.out.println(datagramPacket.getPort());
             return datagramPacket;
	     }
	     
	     public void sendResponse(DatagramPacket datagramPacket) throws Exception {
	    	 DataParser dataParser = new DataParser(datagramPacket.getData());
             Request request = dataParser.parseRequest();
	    	 //DatagramSocket unicastSocket = new DatagramSocket();
	    	 if(request.getCode().equals(Code.GET)) {
              	 Response response = Response.createResponse(request, ResponseCode.CHANGED);
              	 //System.out.println(response.getTokenString());
              	 response.setType(Type.NON);
              	 response.setPayload("response");
              	 Serializer serializer1 = new Serializer();
                 RawData sendResponse = serializer1.serialize(response); 
                 DatagramPacket responsePacket = new DatagramPacket(sendResponse.getBytes(), sendResponse.getSize(), datagramPacket.getAddress(), 5683);
                 System.out.println();
                 System.out.println("==========进入response==========");
                 unicastSocket.send(responsePacket);
                 System.out.println("==========response发送完成==========");
               }
	     }
      /** 
       * @param args 
       * @throws UnknownHostException  
       */  
      public static void main ( String [] args ) throws Exception  
      {  
    	  BroadCastReciever2 reciever = new BroadCastReciever2();
    	  DatagramSocket unicastSocket = new DatagramSocket();
    	  MulticastSocket ms = new MulticastSocket(5683);
    	  reciever.setDatagramSocket(unicastSocket);
    	  reciever.setMulticastSocket(ms);
    	  reciever.setSleepValue(0, 3600, 0, 2400);
          InetAddress group = InetAddress.getByName("228.5.6.7");  
         // new Awake(reciever.getDatagramSocket(),reciever.getMulticastSocket());
          //加入组
          reciever.joinInGroup(group);
          //注册消息，注册时将睡眠特性传给proxy
          reciever.sendRegisterPacket();
	      //接收ACK
	      reciever.recieveACK();
	      //接收request
          while(true){  
               DatagramPacket datagramPacket = reciever.recieveRequest();
               reciever.sendResponse(datagramPacket);  
          }        
      }  
  
}  
