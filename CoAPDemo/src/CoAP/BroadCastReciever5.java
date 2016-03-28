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

import javax.sound.midi.Receiver;

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

//˯�߽ڵ��첽֪ͨ�����ڽڵ�˯�����ڽ����󣬻��������һ��awake���ģ���ʾ�Լ��Ѿ������������յ���������˯�߽ڵ㵥��һ��request
public class BroadCastReciever5  
{  
	     int sleepState;
	     int sleepDuration; //˯������,��λ����ms
	     int timeSleeping;  //��˯��ʱ��
	     int nextSleep; //��һ��˯�߷�����ʱ�䣬����sleepState=0�����
	     InetAddress address;
	     DatagramSocket unicastSocket; //����socket
	     MulticastSocket ms; //�ಥsocket
	     MulticastSocket awakeSocket; //�������ջ�����Ϣ��ģ��͹����ŵ�
	     boolean isRecieve;
	     boolean isAwake;
	     
	     public BroadCastReciever5() throws Exception {
	    	 unicastSocket = new DatagramSocket();
	    	 ms = new MulticastSocket(5683);
	    	 awakeSocket = new MulticastSocket(5688);
	    	 isRecieve = false;
	    	 isAwake = true;
	     }
	     public BroadCastReciever5(InetAddress address,int sleepState,int sleepDuration,int timeSleeping,int nextSleep,boolean isRecieve) throws Exception {
	    	 this.address = address;
	    	 this.sleepState = sleepState;//val=1,��ʾsleep;val=0,��ʾawake
	    	 this.sleepDuration = sleepDuration;
	    	 this.timeSleeping = timeSleeping;
	    	 this.nextSleep = nextSleep;
	    	 this.isRecieve = false;
	     }
	     public BroadCastReciever5(int sleepState,int sleepDuration,int timeSleeping,int nextSleep) throws Exception {
	    	 unicastSocket = new DatagramSocket();
	    	 ms = new MulticastSocket(5683);
	    	 isRecieve = false;
	    	 this.sleepState = sleepState;//val=1,��ʾsleep;val=0,��ʾawake
	    	 this.sleepDuration = sleepDuration;
	    	 this.timeSleeping = timeSleeping;
	    	 this.nextSleep = nextSleep;
	     }
	     public void setSleepValue(int sleepState,int sleepDuration,int timeSleeping,int nextSleep) {
	    	 this.sleepState = sleepState;//val=1,��ʾsleep;val=0,��ʾawake
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
	     /*public void sendAwakePacket() throws Exception{
	    	  Request awake = new Request(Code.POST,Type.NON);
	    	  awake.setToken("0101003".getBytes());
	          OptionSet options = new OptionSet();
	  		  options.setUriQuery("awaking");
	  		  awake.setOptions(options);
		  	  DataSerializer serializer = new DataSerializer();
		      byte[] sendMessage = serializer.serializeRequest(awake);
		      InetAddress proxyAddress = InetAddress.getByName("10.103.241.137");
		      DatagramPacket awakePacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, 5688);
		      //DatagramSocket unicastSocket = new DatagramSocket();
		      System.out.println();
		      System.out.println("==========����awake����==========");
		      unicastSocket.send(awakePacket);
		      System.out.println("==========awake�������==========");
	     }*/
	    /* public void sleeping() {
	    	 ms.close();
	    	 unicastSocket.close();
	     }
	     public void awaking() throws IOException {
	    	 //if(ms.isClosed()==true) 
	    		 this.ms = new MulticastSocket(5683);
	    	 //if(unicastSocket.isClosed()==true) 
	    		 this.unicastSocket = new DatagramSocket();
	     }*/
	    /*public static class Sleep {
	    	 Timer timer;
	    	 public Sleep(BroadCastReciever5 reciever) {
	    		 timer = new Timer();
	    		 timer.schedule(new TimerTask() {
	    			 public void run() {
	    				 System.out.println("sleeping");
	    				 reciever.sleep();
	    			 }
	    		 }, 8000,13600);
	    	 }
	     }
	     public static class Awake {
	    	 Timer timer;
	    	 public Awake(BroadCastReciever5 reciever) {
	    		 timer = new Timer();
	    		 timer.schedule(new TimerTask() {
	    			 public void run() {
							try {
								reciever.awake();
								System.out.println("===============awaking=============");
							} catch (IOException e) {
								System.out.println("����ʧ�ܣ�����˯����");
							}							
	    			 }
	    		 },3000,13600);
	    	 }
	     }*/
	    /* public static class Sleep {
	    	 Timer timer;
	    	 public Sleep(BroadCastReciever5 reciever) {
	    		 timer = new Timer();
	    		 timer.schedule(new TimerTask() {
	    			 public void run() {
	    				 System.out.println("sleeping");
	    				 reciever.sleep();
	    			 }
	    		 }, 8000);
	    	 }
	     }*/
	     public void joinInGroup(InetAddress group) throws IOException {
	    	  System.out.println("==========���������==========");
	          awakeSocket.joinGroup(group); //�͹����ŵ�Ҳ�������
	          ms.joinGroup(group);//�������  
	          System.out.println("==========�ɹ�������==========");
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
		      System.out.println("==========����register==========");
		      unicastSocket.send(registerPacket);
		      System.out.println("==========register�������==========");
	     }
	     
	     public void recieveACK() throws Exception {
	    	  byte[] arb = new byte[1024];
	    	  //DatagramSocket unicastSocket = new DatagramSocket();
	    	  DatagramPacket ACKPacket =new DatagramPacket(arb,arb.length); 
		      System.out.println("==========������ճɹ�ע��ACK==========");
		      unicastSocket.receive(ACKPacket);
		      System.out.println("=========������ɣ�ע��ɹ�=========");
	     }
	     
	     public void recieveAwakeRequest() throws IOException {
	    	 byte[] arb = new byte[1024];
	    	 DatagramPacket p = new DatagramPacket(arb,arb.length);
	    	 awakeSocket.receive(p);
	    	 /*byte[] arb1 = p.getData();
	 		 DataParser dataParser = new DataParser(arb1);
	         Request request = dataParser.parseRequest();
	         System.out.println(request.getOptions().getUriPathString());*/
	    	 System.out.println("���ջ��ѱ������");
	     }
	     public void awake() throws Exception {
	    	 //this.ms = new MulticastSocket(5683);
	    	 isAwake = true;
	    	 System.out.println("�ڵ��ѱ�����");
	    	 DatagramPacket requestPacket = recieveRequest();
	    	 sendResponse(requestPacket);
	    	 ms.setSoTimeout(3000);
	    	 sleep();
	     }
	     public void sleep() {
	    	//this.ms.close();
	    	 System.out.println("�ڵ����˯��״̬");
	    	 isAwake = false;
	    	/* try {
				recieveAwakeRequest();
				System.out.println("���յ����ѱ���");
				awake();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("δ�յ����ѱ���");
				//e.printStackTrace();
			}*/
	     }
	     public DatagramPacket recieveRequest() throws Exception {
	    	 //MulticastSocket ms = new MulticastSocket(5683);
	    	/* recieveAwakeRequest();
	    	 if(isAwake == false) {
	    		 awake();
	    	 }*/
	    	 byte[] arb = new byte[1024];
	    	 DatagramPacket datagramPacket = new DatagramPacket(arb,arb.length);
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
                 System.out.println("==========����response==========");
                 unicastSocket.send(responsePacket);
                 System.out.println("==========response�������==========");
               }
	     }
      /** 
       * @param args 
       * @throws UnknownHostException  
       */  
      public static void main ( String [] args ) throws Exception  
      {  
    	  BroadCastReciever5 reciever = new BroadCastReciever5();
    	 /* DatagramSocket unicastSocket = new DatagramSocket();
    	  MulticastSocket ms = new MulticastSocket(5683);
    	  reciever.setDatagramSocket(unicastSocket);
    	  reciever.setMulticastSocket(ms);*/
    	  reciever.setSleepValue(0, 3600, 0, 2400);
          InetAddress group = InetAddress.getByName("228.5.6.7");  
         /* Thread mainThread = Thread.currentThread();*/
         /* new Sleep(reciever);*/
         /* new Awake(reciever);*/
         /* System.out.println(reciever.getDatagramSocket().isClosed());*/
         // new Awake(reciever.getDatagramSocket(),reciever.getMulticastSocket());
          //������
          reciever.joinInGroup(group);
          //ע����Ϣ��ע��ʱ��˯�����Դ���proxy
          reciever.sendRegisterPacket();
	      //����ACK
	      reciever.recieveACK();
	      //����request
         /* while(true){  
                 
          }     */  
	      reciever.sleep();
      }  
  
}  
