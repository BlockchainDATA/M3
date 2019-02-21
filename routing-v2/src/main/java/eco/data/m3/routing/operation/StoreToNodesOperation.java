package eco.data.m3.routing.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.content.MContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.StoreInitReplyMessage;
import eco.data.m3.routing.message.StoreInitMessage;

/**
 * Operation that stores a DHT Content onto the closest nodes to the content Key
 * 
 * @author xquan
 *
 */
public class StoreToNodesOperation extends MessageHandler implements IOperation {

    private static final Logger logger =
        LoggerFactory.getLogger(StoreToNodesOperation.class.getName());
    
	private final MNode localNode;
	private final MContent content;
	private List<MId> nodesToStore;
	private List<MId> nodesStored = new ArrayList();
	
	private HashMap<MId, Boolean> nodeStoreMap = new HashMap<>();

	public StoreToNodesOperation(MNode localNode, MContent content, List<MId> nodesToStore) {
		this.localNode = localNode;
		this.content = content;
		this.nodesToStore = nodesToStore;
	}

	public StoreToNodesOperation(MNode localNode, MContent content, MId nodeToStore) {
		this.localNode = localNode;
		this.content = content;
		this.nodesToStore = new ArrayList<>();
		nodesToStore.add(nodeToStore);
	}

	@Override
	public void execute() throws Throwable {

		/* Create the message */
		Message msg = new StoreInitMessage(this.content);

		/* Store the message on all of the K-Nodes */
		for (MId n : nodesToStore) {
			// logger.debug("Store Data to " + n );
			if (n.equals(this.localNode.getNodeId())) {
				/* Store the content locally */
				localNode.getDHT().store(this.content);
				callNodeStoredFinished(n);
				nodesStored.add(n);
			} else {
				PeerLink link = localNode.getNetService().getPeerLink(n);
				if(link!=null) {
					link.sendMessage(msg, this);
					nodesStored.add(n);
				}
			}
			// logger.debug("Store Data to " + n + ", Done");
		}
		
		while(true) {
			if(nodeStoreMap.keySet().size()>=nodesToStore.size()){
				break;
			}
			
			synchronized (this) {
				wait(1000);
			}
		}
	}

	public List<MId> getNodesStored() {
		return nodesStored;
	}
	
	private void callNodeStoredFinished(MId nodeId) {	
		synchronized(this) {
			nodeStoreMap.put(nodeId, true);
			notify();
		}
	}
	
	@Override
	public boolean shouldHandleInstantly() {
		return false;
	}

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
		StoreInitReplyMessage msg = (StoreInitReplyMessage) incoming;
		if(msg.isNeedStore()) {
			int max_payload = link.getMaxDataPayload() - StoreContentMessage.getHeaderSize(); // Header len is 13
			
			int start = 0;
			int maxStart = content.getData().length;
			while(start<maxStart) {
				int len = Math.min(content.getData().length-start, max_payload);
				byte [] data = Arrays.copyOfRange(content.getData(), start, start+len);
				StoreContentMessage cm = new StoreContentMessage(msg.getFd(), start, data);
				start += len;
				if(start>=maxStart)
					cm.setEndOfFile(true);
				link.sendMessage(cm, null);
			}
		}
		
		callNodeStoredFinished(link.getRemoteMId());
	}

	@Override
	public void timeout(PeerLink link, Message msg) {
		callNodeStoredFinished(link.getRemoteMId());
	}

}
