package CoAP;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.DataSerializer;
import org.eclipse.californium.core.network.serialization.Serializer;
import org.eclipse.californium.elements.RawData;

public class GroupCommunicationProxy2 extends Thread implements Serializable{
	static int count = 0;
	DatagramSocket s;
	DatagramSocket awakeSocket;
	MulticastSocket ms;	
	DatagramPacket request; //缓存在代理当中的request
	//没有设置数组，只要是先为了简单，不然准确的做法应该是下面两行，利用数组来保存当前的entity的信息，为了在线程中控制当前的entity参数值
	Entity3 entity1; 
	/*Entity3[] entityArr;
	static int entityNum;*/
	public GroupCommunicationProxy2(Entity3 entity1) throws Exception {
		this.s = new DatagramSocket(5683);
		this.ms = new MulticastSocket();
		this.awakeSocket = new DatagramSocket(5688);
		this.entity1 = entity1;
	}
	public void run() {
		while(true) {
			byte[] arb = new byte[1024];
			DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length); 
			try {
				this.awakeSocket.receive(datagramPacket);
				System.out.println("接收到awake报文");
				//将该节点的睡眠状态改为awake
				setNodeSleepState(this.entity1,datagramPacket.getAddress(),0);
				DatagramPacket requestDatagramPacket = this.getRequest();
				//对发来awake报文的节点进行request转发
				if(requestDatagramPacket!=null)
					forwardRequestUnicastToSleepyNode(requestDatagramPacket,datagramPacket.getAddress());
				//new timeToSleep();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*public static class timeToSleep {
   	 Timer timer;
   	 public timeToSleep() {
   		 timer = new Timer();
   		 timer.schedule(new TimerTask() {
   			 public void run() {
   				 //将刚刚awake节点的睡眠状态改为sleep
   				setNodeSleepState(this.entity1,datagramPacket.getAddress(),0);
   			 }
   		 },3600);
   	 }
    }*/
	//缓存目前得到的请求
	public void cacheRequest(DatagramPacket request) {
		this.request = request;
	}
	public DatagramPacket getRequest() {
		return this.request;
	}
	public DatagramPacket recievePacket() throws IOException {
		System.out.println("成功运行recieve");
		byte[] arb = new byte[1024];
		DatagramPacket datagramPacket =new DatagramPacket(arb,arb.length);   
        s.receive(datagramPacket);
        System.out.println("成功运行recieve");
        return datagramPacket;
	}
	
	public Request getRequest(DatagramPacket datagramPacket) {
		byte[] arb = datagramPacket.getData();
		DataParser dataParser = new DataParser(arb);
        Request request = dataParser.parseRequest();
        return request;
	}
	public void HandleRegister(Entity3 entity1,DatagramPacket datagramPacket) throws Exception {
		int sleepState = 0,sleepDuration=0,timeSleeping=0,nextSleep=0;
        Request request = getRequest(datagramPacket);
        //获得睡眠特性参数
    	String payload = request.getPayloadString();
    	if(payload!="") {
	    	String[] payloadArr = payload.split(" ");
	    	sleepState = Integer.parseInt(payloadArr[1]);
	    	sleepDuration = Integer.parseInt(payloadArr[3]);
	    	timeSleeping = Integer.parseInt(payloadArr[5]);
	    	nextSleep = Integer.parseInt(payloadArr[7]);
    	}
    	System.out.println("register报文解析=======节点的睡眠特性");
    	System.out.println("sleepState : " + sleepState);
    	System.out.println("sleepDuration : " + sleepDuration);
    	System.out.println("timeSleeping : " + timeSleeping);
    	System.out.println("nextSleep : " + nextSleep);
    	BroadCastReciever3 node = new BroadCastReciever3(request.getSource(),sleepState,sleepDuration,timeSleeping,nextSleep,false);
		//entity1.groupNodes2.put(request.getSource(), false);
    	//entity1.groupNum++;
    	entity1.getGroup().addGroupMember(node);
    	
    	//回复ACK,同步时钟
    	Response ACKResponse = Response.createResponse(request, ResponseCode.CREATED);
    	ACKResponse.setType(Type.ACK);
    	
    	System.out.println(ACKResponse);
    	Serializer serializer1 = new Serializer();
    	
        RawData sendACKResponse = serializer1.serialize(ACKResponse); 
        System.out.println(datagramPacket.getAddress());
        DatagramPacket ACKPacket = new DatagramPacket(sendACKResponse.getBytes(), sendACKResponse.getSize(), datagramPacket.getAddress(), datagramPacket.getPort());
     	s.send(ACKPacket);
	}
	//接收到awake报文后，立即向睡眠节点转发当前的request
	public void forwardRequestUnicastToSleepyNode(DatagramPacket request,InetAddress sleepyNodeAddress) throws IOException {
		DatagramPacket unicastPacket = new DatagramPacket(request.getData(),request.getLength(),sleepyNodeAddress,5683);
		s.send(unicastPacket);
		//将中间过程省略，CON的CoAP单播报文，假定可靠，不再进行中间一系列的CON、ACK的传输
		setIsRecieve(entity1,sleepyNodeAddress);
	}
	public void forwardRequestUnicast(DatagramPacket datagramPacket,Entity3 entity1) throws IOException {
		Group3 group = entity1.getGroup();
		Set<BroadCastReciever3> groupMember = group.getGroupMember();
		for(BroadCastReciever3 node : groupMember) {
			InetAddress nodeAddress = node.getInetAddress();
			DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
    		System.out.println(unicastPacket);
    		s.send(unicastPacket);
    		node.setIsRecieve(true); //直接将节点状态改变
		}
		for(int i=0;i<entity1.nodes.length;i++) {
    		InetAddress nodeAddress = entity1.nodes[i].getInetAddress();
    		DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
    		System.out.println(unicastPacket);
    		s.send(unicastPacket);
    	}
		System.out.println("单播完成");
	}
	public void forwardRequestMulticast(DatagramPacket datagramPacket,Entity3 entity1) throws IOException {
		InetAddress multiAddress = entity1.getGroup().getInetAddress(); 
		/*sendAwakePacket(multiAddress);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("等待出错");
		}*/
		System.out.println("是否转发request？yes no");
		Scanner in = new Scanner(System.in);
		String option = in.nextLine();
		if(option=="yes") {
    	DatagramPacket forwardPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), multiAddress, 5683);
    	ms.send(forwardPacket);
    	if(entity1.nodes != null){
    		for(int i=0;i<entity1.nodes.length;i++) {
        		InetAddress nodeAddress = entity1.nodes[i].getInetAddress();
        		DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
        		System.out.println(unicastPacket);
        		s.send(unicastPacket);
        	}
    	}}
	}
	public void recieveResponse(Entity3 entity1) throws Exception {
		byte[] arb1 = new byte[1024];
		s.setSoTimeout(1000);
		System.out.println("=============");
		DatagramPacket responsePacket =new DatagramPacket(arb1,arb1.length);
        s.receive(responsePacket);
        System.out.println("&&&&&&&&&&&&");
        Group3 group = entity1.getGroup();
        Set<BroadCastReciever3> groupMember = group.getGroupMember();
        for(BroadCastReciever3 node : groupMember) {
        	if(node.getInetAddress() == responsePacket.getAddress() && node.getIsRecieve()==false) 
        		node.setIsRecieve(true);
        }
	}
	public void retransmitRequestMulticast(Entity3 entity1,DatagramPacket datagramPacket) throws IOException {
		InetAddress multiAddress = entity1.group.getInetAddress(); 
    	DatagramPacket forwardPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), multiAddress, 5683);
    	ms.send(forwardPacket);
	}	
	public void retransmitRequestUnicast(Entity3 entity1,DatagramPacket datagramPacket) throws IOException {
		Set<BroadCastReciever3> groupMember = entity1.getGroup().getGroupMember();
		for(BroadCastReciever3 node : groupMember){
        	//System.out.println("**************************");
        	if(node.getIsRecieve() == false) {
        		InetAddress nodeAddress = node.getInetAddress();
        		DatagramPacket unicastPacket = new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), nodeAddress, 5683);
        		System.out.println(unicastPacket);
        		s.send(unicastPacket);
        		node.setIsRecieve(true);//直接将节点状态改变
        	}
        }
	}
	
	public void sendResponse(Request request) throws IOException {
		 Response finalResponse = Response.createResponse(request, ResponseCode.CHANGED);
         finalResponse.setType(Type.NON);
         finalResponse.setPayload("finalResponse");
      	 Serializer serializer1 = new Serializer();
         RawData sendFinalResponse = serializer1.serialize(finalResponse); 
         InetAddress DesAddress = InetAddress.getByName("10.103.240.31");  
         DatagramPacket resPacket = new DatagramPacket(sendFinalResponse.getBytes(), sendFinalResponse.getSize(),DesAddress,5683);
         s.send(resPacket);
	}
	
	/*public void sendAwakePacket(InetAddress multiAddress) throws IOException{
		int port = 5688;
		Request awakeRequest = new Request(Code.POST,Type.NON);
        byte[] bToken = ("010011".getBytes());
        awakeRequest.setToken(bToken);
        OptionSet options = new OptionSet();
        options.setUriPath("Entity1");
        options.setUriQuery("awake");
        options.setProxyUri("10.103.243.115");
        awakeRequest.setOptions(options);
		DataSerializer serializer = new DataSerializer();
        byte[] sendMessage = serializer.serializeRequest(awakeRequest);
        DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, multiAddress, port);
        ms.send(datagramPacket);
	}*/
	
	//将节点此刻的状态置为1，表示已成功接收到request
	public void setIsRecieve(Entity3 entity1,InetAddress address) {
		Group3 group = entity1.getGroup();
		Set<BroadCastReciever3> groupMember = group.getGroupMember();
		for(BroadCastReciever3 node : groupMember) {
        	if(node.getInetAddress() == address) 
        		node.setIsRecieve(true);
        }
		if(entity1.nodes != null) {
			for(int i=0;i<entity1.nodes.length;i++) {
	    		InetAddress nodeAddress = entity1.nodes[i].getInetAddress();
	    		if(nodeAddress==address) {
	    			entity1.nodes[i].setIsRecieve(true);
	    		}
	    	}
		}
	}
	
	//将节点的睡眠状态设置为睡眠即1
	public void setNodeSleepState(Entity3 entity1,InetAddress address,int sleepState) {
		Group3 group = entity1.getGroup();
		Set<BroadCastReciever3> groupMember = group.getGroupMember();
		for(BroadCastReciever3 node : groupMember) {
        	if(node.getInetAddress() == address) 
        		node.setSleepState(1);
        }
	}
	
	public static void main(String[] args) throws Exception {
		//GroupCommunicationProxy2 gcProxy = new GroupCommunicationProxy2();
		/*GroupCommunicationProxy2.MyThread myThread = gcProxy.new MyThread();
		myThread.run();*/
		
		System.out.println("成功运行代理");
		//创建一个Entity
		try {
			Group3 group = new Group3(InetAddress.getByName("228.5.6.7"));
			BroadCastReciever3[] nodes = null;
			//nodes[0].setInetAddress(InetAddress.getByName("10.103.241.142"));
			Entity3 entity1 = new Entity3(group,nodes,"Entity1");
			GroupCommunicationProxy2 gcProxy = new GroupCommunicationProxy2(entity1);		
			//创建一个接收报文
	        byte[] arb = new byte[1024];       
	        boolean flag = true;
	        
        while(true){ 
        	gcProxy.start();
        	System.out.println("我没有被阻塞，已经成功运行");
        	//接收报文
        	DatagramPacket datagramPacket = gcProxy.recievePacket();
            Request request = gcProxy.getRequest(datagramPacket);
            gcProxy.cacheRequest(datagramPacket);
        	//接收并处理register	    
            //System.out.println(request.getOptions().getUriPathString());
            if(request.getOptions().getUriPathString() == "") {
	            String URIQuery = request.getOptions().getUriQueryString();
	            System.out.println(URIQuery);
	            if(URIQuery.substring(0, 8).equals("register")) {
	            	System.out.println(count++);
	            	gcProxy.HandleRegister(entity1,datagramPacket);
	            	continue ;
	            }
            }else{
            	System.out.println("接收到sender端的request");
            }
        	//转发request
            String entityURI = request.getOptions().getUriPathString();
            String forwardMethod = request.getPayloadString();
            if(entityURI.equals(entity1.getName())) {
            	if(forwardMethod.equals("2")) {
            		gcProxy.forwardRequestUnicast(datagramPacket,entity1);
            		continue;
            	}else {	
	            	gcProxy.forwardRequestMulticast(datagramPacket, entity1);
            	}
            } 
            System.out.println("已运行");
            
            int sum = 0; //要来记录目前已经接收到的response的个数
            //接收response
            for(int i = 0;i < entity1.getGroup().getGroupNum();i++) {
            	System.out.println("进入response" + i);
            	try{
            		gcProxy.recieveResponse(entity1); 
            		sum++;
                }catch(Exception e){
                	//e.printStackTrace();
                	if(forwardMethod.equals("3")) {
	                	if(sum < entity1.getGroup().getGroupNum()/2 ) { //多播重发
	                		gcProxy.retransmitRequestMulticast(entity1,datagramPacket);
	                    	i = 0;
	                		continue;
	                	}else {   //单播重发
	                		gcProxy.retransmitRequestUnicast(entity1,datagramPacket);
	                	}
                	}else {
                		gcProxy.retransmitRequestMulticast(entity1,datagramPacket);
                    	i = 0;
                		continue;
                	}
                }      	
            }
            
            /*System.out.println("#####################"+entity1.groupNodes2.isEmpty());
            
            for(Entry<InetAddress, Boolean> entity4 : entity1.groupNodes2.entrySet()){
            	System.out.println("**************************");
            	System.out.println(entity4.getKey() + "    "  + entity4.getValue());
            }*/
            
            //确认收到所有的response之后，回复给sender端一个response
            gcProxy.sendResponse(request);
            
            //执行完之后需要将组内节点的状态全部置为false
            for(BroadCastReciever3 node : entity1.getGroup().getGroupMember()){
            	node.setIsRecieve(false);
            }
        }
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
		
}
