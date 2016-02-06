package pt.ipb.sd;

import java.io.Serializable;

import org.jgroups.Address;

@SuppressWarnings("serial")
public class PeerInfo implements Comparable<PeerInfo>, Serializable{

	long logicalClock;
	String guid;
	Address address;

	PeerInfo(String guid, Address address) {
		this.guid = guid;
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public long getLogicalClock() {
		return logicalClock;
	}
	public void setLogicalClock(long logicalClock) {
		this.logicalClock = logicalClock;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public void incrementLogicalClock() {
		this.logicalClock++;
	}
	
	public int compareTo(PeerInfo comparePeerInfo) {
		int compareLogicalClock = (int) comparePeerInfo.getLogicalClock();
		
		// Descending order
		return (int) (compareLogicalClock - this.logicalClock);
	}
}
