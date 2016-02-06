package pt.ipb.sd;

import java.util.LinkedList;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class Peer extends ReceiverAdapter {

	private JChannel channel;

	PeerInfo peerInfo; // Peer information, contains GUID and logicalClock

	LinkedList<PeerInfo> ReqQ; // Request Queue
	LinkedList<PeerInfo> AckQ; // Acknowledge Queue

	private long numberOfPeersInCluster;

	public enum State {
		ready, waiting, inCriticalSection
	}

	public State state;

	Peer() {
		ReqQ = new LinkedList<PeerInfo>();
		AckQ = new LinkedList<PeerInfo>();
		this.state = State.ready;
	}

	public void start() throws Exception {
		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect("MyCluster");

		try {
			// Obtain Peer id in Cluster
			this.peerInfo = new PeerInfo(channel.getAddressAsUUID(), channel.getAddress());
		} catch (Exception e) {
			System.out.println("ERROR: Could not obtain Peer ID\nException: " + e);
		}
	}

	public void request() {

		// Send message request to Cluster
		Message msg = new Message(null, null, this.peerInfo);
		try {
			channel.send(msg);
			ReqQ.add(this.peerInfo);
			AckQ.add(this.peerInfo);
			this.peerInfo.incrementLogicalClock();

			this.state = State.waiting;
			System.out.println(
					this.peerInfo.getGuid() + ": " + this.state + ", Clock: " + this.peerInfo.getLogicalClock());

		} catch (Exception e) {
			System.out.println("ERROR: Could not send message to Cluster.\nException: " + e);
		}
	}

	// Receive messages from other Peer's
	@Override
	public void receive(Message msg) {

		this.peerInfo.incrementLogicalClock();
		System.out.println(this.peerInfo.getGuid() + ": " + this.state + ", Clock: " + this.peerInfo.getLogicalClock());
		
		// Obtain PeerInfo from message
		PeerInfo peerInfo = (PeerInfo) msg.getObject();

		// Synchronize logicalClock
		if (this.peerInfo.getLogicalClock() < peerInfo.getLogicalClock()) {
			this.peerInfo.setLogicalClock(peerInfo.getLogicalClock());
		}

		// Only replies if the Peer is not in CS nor waiting for a Reply
		if (this.state == State.ready) {
			reply(msg.getSrc(), this.peerInfo);
		}

		// If Peer is waiting for replies to enter Critical Section
		else if (this.state == State.waiting) {
			// Obtain number of connected Peer's in Cluster
			this.numberOfPeersInCluster = channel.getView().getMembers().size();
			
			// Message is a Request when msg.getDest() == null (multicast)
			if(msg.getDest() == null) {
				if(ReqQ.isEmpty()) {
					ReqQ.add(peerInfo);
				} else {
					// Once ReqQ.contains(peerInfo) does not work as we want...
					boolean contains = false;
					for (PeerInfo item : ReqQ) {
						if(peerInfo.getGuid().equals(item.getGuid())) {
							contains = true;
						}
					}
					if(!contains){
						ReqQ.add(peerInfo);
					}
				}
			} 
			
			// msg.getDest() != null -> The message is a Reply
			else {
				// Once AckQ.contains(peerInfo) does not work the way we want...
				if (AckQ.isEmpty()) {
					AckQ.add(peerInfo);
				} else {
					// Once AckQ.contains(peerInfo) does not work as we want...
					boolean contains = false;
					for (PeerInfo item : AckQ) {
						if(peerInfo.getGuid().equals(item.getGuid())) {
							contains = true;
						}
					}
					if(!contains){
						AckQ.add(peerInfo);
					}
				}
			}			

			if (this.numberOfPeersInCluster == AckQ.size()) {

				this.state = State.inCriticalSection;
				System.out.println(
						this.peerInfo.getGuid() + ": " + this.state + ", Clock: " + this.peerInfo.getLogicalClock());
				try {
					Thread.sleep(5000); // Simulate a Critical Section
				} catch (InterruptedException e) {
					System.out.println("Could not sleep.\nException: " + e);
				}
				this.state = State.ready;
				System.out.println(
						this.peerInfo.getGuid() + ": " + this.state + ", Clock: " + this.peerInfo.getLogicalClock());

				// Reply to Requester's with lowest logicalClock
				// NEEDS TO GIVE PRIORITY TO LOWEST LOGICALCLOCK <<<<<<
				if(!this.ReqQ.isEmpty()) {
					for (PeerInfo item : ReqQ) {
						if (!item.getGuid().equals(this.peerInfo.getGuid())) {
							reply(item.getAddress(), this.peerInfo);
						}
						this.ReqQ.remove(item);
					}
				}
			}
		}
	}

	public void reply(Address msgSrcAddress, PeerInfo peerInfo) {
		Message msg = new Message(msgSrcAddress, peerInfo);
		try {
			channel.send(msg);
			this.peerInfo.incrementLogicalClock();
		} catch (Exception e) {
			System.out.println("Could not send reply message.\nException: " + e);
		}
	}

	// Getters and Setters
	public JChannel getChannel() {
		return channel;
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public String getState() {
		return this.state.toString();
	}

	public void setState(State state) {
		this.state = state;
	}

	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	public LinkedList<PeerInfo> getReqQ() {
		return ReqQ;
	}

	public void setReqQ(LinkedList<PeerInfo> reqQ) {
		ReqQ = reqQ;
	}

	public LinkedList<PeerInfo> getAckQ() {
		return AckQ;
	}

	public void setAckQ(LinkedList<PeerInfo> ackQ) {
		AckQ = ackQ;
	}

	public long getNumberOfPeersInCluster() {
		return numberOfPeersInCluster;
	}

	public void setNumberOfPeersInCluster(long numberOfPeersInCluster) {
		this.numberOfPeersInCluster = numberOfPeersInCluster;
	}
}
