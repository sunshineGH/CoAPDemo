package CoAP;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Group3 {
	InetAddress groupAddress; 
    int groupNum = 0;
	Set<BroadCastReciever3> nodes = new HashSet<BroadCastReciever3>();
	//BroadCastReciever[] nodes;
	public Group3(InetAddress inetAddress){
		this.groupAddress = inetAddress;
	}
	public void setInetAddress(InetAddress groupAddress) {
		this.groupAddress = groupAddress;
	}
	public InetAddress getInetAddress() {
		return groupAddress;
	}
	public void setGroupNum(int num) {
		groupNum = num;
	}	
	public int getGroupNum() {
		return groupNum;
	}
	public Set<BroadCastReciever3> getGroupMember() {
		return this.nodes;
	}
	public void addGroupMember(BroadCastReciever3 node) {
		nodes.add(node);
	}
	public void removeGroupMember(BroadCastReciever3 node) {
		nodes.remove(node);
	}	
}
