package CoAP;

import java.net.DatagramPacket;  
import java.net.DatagramSocket;
import java.net.InetAddress;  
import java.net.MulticastSocket;  
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.network.Exchange.Origin;
import org.eclipse.californium.core.network.serialization.DataParser;
import org.eclipse.californium.core.network.serialization.DataSerializer;
import org.eclipse.californium.core.network.serialization.Serializer;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.proxy.DirectProxyCoAPResolver;
/** 
 *  
 * 单播发送端 
 
 */  
public class UnicastSend2  
{  
	  public void setRequest() {
		  
	  }
      /** 
       * @param args 
       * @throws UException  
       */  
      public static void main ( String [] args ) throws Exception  
      {  
            int port = 5683;
            Scanner in = new Scanner(System.in);
            System.out.println("重传方式选择：");
            System.out.println("1.多播重传");
            System.out.println("2.单播重传");
            System.out.println("3.多播+单播重传");
            System.out.print("请输入您的选择：");
            Integer op = in.nextInt();
            System.out.println("您输入的数字是" + op);
            //String sendMessage= "Hello world";
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
 
           /* DirectProxyCoAPResolver proxy = new DirectProxyCoAPResolver();
            Exchange exchange = new Exchange(request,Origin.REMOTE);
            proxy.forwardRequest(exchange);*/
            
            DataSerializer serializer = new DataSerializer();
            byte[] sendMessage = serializer.serializeRequest(request);
            //System.out.println("sender multicating: " + request.isMulticast());
            //System.out.print(request.getType());
            
            //String sendMessage = request.toString();
            InetAddress inetAddress = InetAddress.getByName("228.5.6.7");  
            InetAddress proxyAddress = InetAddress.getByName("10.103.243.115");
            /*DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, port);*/  
            DatagramPacket datagramPacket = new DatagramPacket(sendMessage, sendMessage.length, proxyAddress, port);
            DatagramSocket datagramSocket = new DatagramSocket(4576); 
            System.out.println("==========进入发送request报文==========");
            datagramSocket.send(datagramPacket);
            System.out.println("==========request发送完成==========");
            System.out.println();
           /* 
            Response response = request.waitForResponse();
            System.out.print(response.toString());*/
           //while(true) {
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
}  