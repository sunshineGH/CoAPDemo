package CoAP;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Entity2 {
	Group2 group; //多播组
	BroadCastReciever2[] nodes; //单播节点
	String entityName; 
	//int groupNum; //多播组成员个数
	//Map<InetAddress,Boolean> groupNodes2 = new HashMap<InetAddress,Boolean>(); //多播组成员记录	
	//List<InetAddress> groupNodes = new LinkedList<InetAddress>(); 	
	//InetAddress[] groupNodes;
	
	Entity2(Group2 group,BroadCastReciever2[] nodes,String name) {
		if(nodes != null && nodes.length > 0){
			this.nodes = new BroadCastReciever2[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				this.nodes[i] = nodes[i];
			}
		}else{
			this.nodes = null;
		}
		this.entityName = name;
		this.group = group;	
		//this.groupNum = 0;		
	}
	public String getName() {
		return this.entityName;
	}
	public Group2 getGroup() {
		return this.group;
	}
	public void setName(String name) {
		this.entityName = name;
	}
	public void setGroup(Group2 group) {
		this.group = group;
	}
}
