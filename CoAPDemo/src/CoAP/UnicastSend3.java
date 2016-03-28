package CoAP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.DataSerializer;

public class UnicastSend3 {
	DatagramSocket s;
	DatagramSocket datagramSocket;
	public UnicastSend3() throws SocketException {
		s = new DatagramSocket();
		datagramSocket = new DatagramSocket(5683);
	}
	public Request setRequest(Integer op) {
		Request request = new Request(Code.GET,Type.NON);
        //request.setURI("228.5.6.7:5683/res");
        //request.setMulticast(true);
        byte[] bToken = ("01001".getBytes());
        request.setToken(bToken);
        request.setPayload(op.toString());
        OptionSet options = new OptionSet();
        options.setUriPath("Entity1");
        options.setUriQuery("close");
        options.setProxyUri("10.103.243.115");
        request.setOptions(options);
        return request;
	}
	public void sendRequest(Request request,InetAddress proxyAddress,int port) throws IOException {
		DataSerializer serializer = new DataSerializer();
        byte[] sendMessage = serializer.serializeRequest(request);
        DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, port);
        System.out.println("==========进入发送request报文==========");
        s.send(datagramPacket);
        System.out.println("==========request发送完成==========");  
	}
	public void recieveResponse() throws IOException {
		 byte[] arb = new byte[1024]; 
	     DatagramPacket responsePacket =new DatagramPacket(arb,arb.length); 
	     System.out.println("==========进入接收final response报文==========");
	     datagramSocket.receive(responsePacket);
	     System.out.println("==========final response报文接收完成==========");
	     System.out.println();
	     DataParser dataParser = new DataParser(arb);
	     Response response = dataParser.parseResponse();
	     System.out.println("==========解析final response报文==========");
	     System.out.println("response payload : " + response.getPayloadString());
	}
	public static void main(String[] args) throws Exception{
		int port = 5683;
		UnicastSend3 sender = new UnicastSend3();
        Scanner in = new Scanner(System.in);
        System.out.println("重传方式选择：");
        System.out.println("1.多播重传");
        System.out.println("2.单播重传");
        System.out.println("3.多播+单播重传");
        System.out.print("请输入您的选择：");
        Integer op = in.nextInt();
        System.out.println("您输入的数字是" + op);
        //String sendMessage= "Hello world";
        Request request = sender.setRequest(op);

        InetAddress inetAddress = InetAddress.getByName("228.5.6.7");  
        InetAddress proxyAddress = InetAddress.getByName("10.103.241.137");
        sender.sendRequest(request,proxyAddress,port);
        System.out.println();
        sender.recieveResponse();
    }                      
}
