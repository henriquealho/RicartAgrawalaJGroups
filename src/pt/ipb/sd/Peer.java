package pt.ipb.sd;

import java.util.LinkedList;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class Peer extends ReceiverAdapter {

	private JChannel channel;
	String guid; // Peer Id
	String address; // Peer Address
	long logicalClock;
	LinkedList<Long> reqQ; // Request Queue
	LinkedList<Long> ackQ; // Acknowledge Queue

	public enum State {
		ready, waiting, inCriticalSection
	}

	public State state;

	Peer() {
		reqQ = new LinkedList<>();
		ackQ = new LinkedList<>();
		state = State.ready; // Initial Peer state -> ready
	}

	public void request() {
		
		// Send message request to Cluster
		Message msg = new Message(null, null, String.valueOf(++logicalClock));
		try {
			channel.send(msg);
			this.state = State.waiting;
			reqQ.add(logicalClock);
		} catch (Exception e) {
			System.out.println("ERROR: Could not send message to channel.\nException: " + e);
		}
	}

	// Receive messages from other Peer's
	@Override
	public void receive(Message msg) {
		this.logicalClock = Long.parseLong(msg.getObject().toString());
		
		System.out.println(msg.getSrc() + ": " + msg.getObject());
	}

	public void reply() {

	}

	public void start() throws Exception {
		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect("MyCluster");
		
		try {
			guid = channel.getAddressAsUUID(); // Obtain Peer id in channel
			address = channel.getAddressAsString(); // Obtain Peer Address in channel
		} catch (Exception e) {
			System.out.println("ERROR: Could not obtain Peer id\nException: " + e);
		}
	}

	// Getters and Setters
	public JChannel getChannel() {
		return channel;
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public long getLogicalClock() {
		return logicalClock;
	}

	public void setLogicalClock(long logicalClock) {
		this.logicalClock = logicalClock;
	}

	public LinkedList<Long> getReqQ() {
		return reqQ;
	}

	public void setReqQ(LinkedList<Long> reqQ) {
		this.reqQ = reqQ;
	}

	public LinkedList<Long> getAckQ() {
		return ackQ;
	}

	public void setAckQ(LinkedList<Long> ackQ) {
		this.ackQ = ackQ;
	}
	
	public String getState() {
		return this.state.toString();
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
}
