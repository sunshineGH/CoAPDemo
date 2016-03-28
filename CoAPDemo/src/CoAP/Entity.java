package CoAP;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Entity {
	InetAddress group; //�ಥ��
	InetAddress[] nodes; //�����ڵ�
	String entityName; 
	int groupNum; //�ಥ���Ա����
	Map<InetAddress,Boolean> groupNodes2 = new HashMap<InetAddress,Boolean>(); //�ಥ���Ա��¼
	
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
