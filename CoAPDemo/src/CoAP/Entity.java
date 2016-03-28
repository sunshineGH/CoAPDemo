package CoAP;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Entity {
	InetAddress group; //多播组
	InetAddress[] nodes; //单播节点
	String entityName; 
	int groupNum; //多播组成员个数
	Map<InetAddress,Boolean> groupNodes2 = new HashMap<InetAddress,Boolean>(); //多播组成员记录
	
	//List<InetAddress> groupNodes = new LinkedList<InetAddress>(); 
	
	//InetAddress[] groupNodes;

	
	Entity(InetAddress group,InetAddress[] nodes,String name) {
		if(nodes != null && nodes.length > 0){
			this.nodes = new InetAddress[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				this.nodes[i] = nodes[i];
			}
		}else{
			this.nodes = null;
		}
		this.entityName = name;
		this.group = group;
		this.groupNum = 0;
		
	}
	
	String getName() {
		return this.entityName;
	}
}
