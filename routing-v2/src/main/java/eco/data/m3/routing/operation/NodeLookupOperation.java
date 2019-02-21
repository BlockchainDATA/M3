package eco.data.m3.routing.operation;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MConfiguration;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;

/**
 * Finds the K closest nodes to a specified identifier The algorithm terminates
 * when it has gotten responses from the K closest nodes it has seen. Nodes that
 * fail to respond are removed from consideration
 * 
 * @author xquan
 *
 */
public class NodeLookupOperation extends MessageHandler implements IOperation {

	private static final Logger logger = LoggerFactory.getLogger(NodeLookupOperation.class.getName());

	private final MNode localNode;
	private final MConfiguration config;

	private final Message lookupMessage; // Message sent to each peer
	
	private LookupQueue loopupQueue;

	private int test_id = new Random().nextInt();

	/**
	 * @param server    KadServer used for communication
	 * @param localNode The local node making the communication
	 * @param lookupId  The ID for which to find nodes close to
	 * @param config
	 */
	public NodeLookupOperation(MNode localNode, MId lookupId) {
		// logger.debug("lookup begin : " + test_id + ", look " + lookupId);

		this.localNode = localNode;
		this.config = localNode.getCurrentConfiguration();

		this.lookupMessage = new NodeLookupMessage(lookupId);
		this.loopupQueue = new LookupQueue(lookupId, this.config.k());
	}

	/**
	 * @throws Throwable
	 */
	@Override
	public void execute() throws Throwable {
		// logger.debug("Node Lookup ");
		/* Set the local node as already asked */
		loopupQueue.put(this.localNode.getNodeId(), LookupStatus.Asked);

		/**
		 * We add all nodes here instead of the K-Closest because there may be the case
		 * that the K-Closest are offline - The operation takes care of looking at the
		 * K-Closest.
		 */
		loopupQueue.addNodes(this.localNode.getRoutingTable().getAllNodes());

		/*
		 * If we haven't finished as yet, wait for a maximum of
		 * config.operationTimeout() time
		 */	
		int totalTimeWaited = 0;
		int timeInterval = 10; // We re-check every n milliseconds
		while (totalTimeWaited < this.config.operationTimeout()) {
			if (!this.askNodesorFinish()) {
				synchronized (this) {
					wait(timeInterval);
					totalTimeWaited += timeInterval;
				}
			} else {
				break;
			}
		}

		// logger.debug("lookup done : " + test_id + " -- " + totalTimeWaited + " -- " +
		// this.lookupId + " -- " + getFailedNodes());

		// localNode.getNetService().printLinks();
		/*
		 * Now after we've finished, we would have an idea of offline nodes, lets update
		 * our routing table
		 */
		this.localNode.getRoutingTable().setUnresponsiveContacts(loopupQueue.getFailedNodes());
	}

    public List<MId> getClosestNodes()
    {
        return this.loopupQueue.closestNodes(LookupStatus.Asked);
    }
	
	/**
	 * Asks some of the K closest nodes seen but not yet queried. Assures that no
	 * more than DefaultConfiguration.CONCURRENCY messages are in transit at a time
	 *
	 * This method should be called every time a reply is received or a timeout
	 * occurs.
	 *
	 * If all K closest nodes have been asked and there are no messages in transit,
	 * the algorithm is finished.
	 *
	 * @return <code>true</code> if finished OR <code>false</code> otherwise
	 * @throws Throwable
	 */
	private boolean askNodesorFinish() throws Throwable {
		int awaitNodeSize = loopupQueue.getAwatingNodes().size();

		/* If >= CONCURRENCY nodes are in transit, don't do anything */
		if (this.config.maxConcurrentMessagesTransiting() <= awaitNodeSize) {
			return false;
		}

		/* Get unqueried nodes among the K closest seen that have not FAILED */
		List<MId> unasked = loopupQueue.closestNodesNotFailed(LookupStatus.UnAsked);
	   
//		if(unasked.size()>0){
//			System.out.println(" %%%%%  %%%%%  %%%%%  %%%%% UnAsked :");
//			for (MId mId : unasked) {
//					System.out.println(mId);
//			}
//		}

		if (unasked.isEmpty() && awaitNodeSize == 0) {
			/* We have no unasked nodes nor any messages in transit, we're finished! */
			return true;
		}

		for (int i = 0; (awaitNodeSize < config.maxConcurrentMessagesTransiting()) && (i < unasked.size()); i++) {
			MId n = (MId) unasked.get(i);
			PeerLink link = null;
			try {
				// if(!n.toString().startsWith("303030303030303030303030303030303030303"))
				// {
				// logger.debug(" start " + test_id + ", " + localNode.getNodeId() + " search "
				// + n );
				// }

//				logger.debug("Lookup with " + n);
				loopupQueue.put(n, LookupStatus.Awating);
				link = localNode.getNetService().getPeerLink(n);
				link.sendMessage(lookupMessage, this, config.operationTimeout());
				
//				logger.debug("Msg Sent :" + lookupMessage.getSrcConvId());
			} catch (Exception e) {
//				logger.debug(" -- Failed -- +" + n);
//				e.printStackTrace();
				/*
				 * Mark this node as failed and inform the routing table that it is unresponsive
				 */

				loopupQueue.put(n, LookupStatus.Failed);
				localNode.getRoutingTable().setUnresponsiveContact(n);
			}
		}

//        unasked = this.closestNodesNotFailed(UNASKED);
//        System.out.println("UnAsked After :");
//        for (MId mId : unasked) {
//			System.out.println(mId);
//		}
		/* We're not finished as yet, return false */
		return false;
	}

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
		NodeReplyMessage msg = (NodeReplyMessage) incoming;

		/* Add the origin node to our routing table */
		this.localNode.getRoutingTable().insert(link.getRemoteMId());

//		logger.debug("Node Reply Handle Begin : " + incoming.getDestConvId());

		synchronized (this) {
			/* Set that we've completed ASKing the origin node */
			this.loopupQueue.put(link.getRemoteMId(), LookupStatus.Asked);
			/* Add the received nodes to our nodes list to query */
			this.loopupQueue.addNodes(msg.getNodes());
			notify();
		}

//		logger.debug("Node Reply Handle End   : " + incoming.getDestConvId());
	}

	/**
	 * A node does not respond or a packet was lost, we set this node as failed
	 *
	 * @param comm
	 * @throws Throwable
	 */
	@Override
	public void timeout(PeerLink link, Message msg) {
		/* Get the node associated with this communication */
		MId n = link.getRemoteMId();

		logger.debug("Time Out : " + msg.getSrcConvId());
		/*
		 * Mark this node as failed and inform the routing table that it is unresponsive
		 */
		this.localNode.getRoutingTable().setUnresponsiveContact(n);
		
		synchronized (this) {
			this.loopupQueue.put(n, LookupStatus.Failed);
			notify();
		}

	}

}
