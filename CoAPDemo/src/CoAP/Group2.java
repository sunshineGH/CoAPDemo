package CoAP;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Group2 {
	InetAddress groupAddress; 
    int groupNum = 0;
	Set<BroadCastReciever2> nodes = new HashSet<BroadCastReciever2>();
	//BroadCastReciever[] nodes;
	public Group2(InetAddress inetAddress){
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
	public Set<BroadCastReciever2> getGroupMember() {
		return this.nodes;
	}
	public void addGroupMember(BroadCastReciever2 node) {
		nodes.add(node);
	}
	public void removeGroupMember(BroadCastReciever2 node) {
		nodes.remove(node);
	}	
}
