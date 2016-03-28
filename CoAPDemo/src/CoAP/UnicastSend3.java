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
        System.out.println("==========���뷢��request����==========");
        s.send(datagramPacket);
        System.out.println("==========request�������==========");  
	}
	public void recieveResponse() throws IOException {
		 byte[] arb = new byte[1024]; 
	     DatagramPacket responsePacket =new DatagramPacket(arb,arb.length); 
	     System.out.println("==========�������final response����==========");
	     datagramSocket.receive(responsePacket);
	     System.out.println("==========final response���Ľ������==========");
	     System.out.println();
	     DataParser dataParser = new DataParser(arb);
	     Response response = dataParser.parseResponse();
	     System.out.println("==========����final response����==========");
	     System.out.println("response payload : " + response.getPayloadString());
	}
	public static void main(String[] args) throws Exception{
		int port = 5683;
		UnicastSend3 sender = new UnicastSend3();
        Scanner in = new Scanner(System.in);
        System.out.println("�ش���ʽѡ��");
        System.out.println("1.�ಥ�ش�");
        System.out.println("2.�����ش�");
        System.out.println("3.�ಥ+�����ش�");
        System.out.print("����������ѡ��");
        Integer op = in.nextInt();
        System.out.println("�������������" + op);
        //String sendMessage= "Hello world";
        Request request = sender.setRequest(op);

        InetAddress inetAddress = InetAddress.getByName("228.5.6.7");  
        InetAddress proxyAddress = InetAddress.getByName("10.103.241.137");
        sender.sendRequest(request,proxyAddress,port);
        System.out.println();
        sender.recieveResponse();
    }                      
}
