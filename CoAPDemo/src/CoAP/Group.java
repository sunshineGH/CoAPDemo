package CoAP;

import java.net.InetAddress;

public class Group {
	InetAddress groupAddress; 
	static int groupNum = 0;
	public Group(InetAddress inetAddress){
		this.groupAddress = inetAddress;
	}
}
