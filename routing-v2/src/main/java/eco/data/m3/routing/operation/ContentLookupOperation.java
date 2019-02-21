package eco.data.m3.routing.operation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.content.MContentKey;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentLookupReplyMessage;
import eco.data.m3.routing.message.NodeReplyMessage;
import eco.data.m3.routing.util.RouteLengthChecker;

public class ContentLookupOperation extends MessageHandler implements IOperation{

    private static final Logger logger =
        LoggerFactory.getLogger(ContentLookupOperation.class.getName());

    private List<MId> nodesFound = new ArrayList<>();

    private final MNode localNode;

	private final ContentLookupMessage lookupMessage;
	
	private LookupQueue loopupQueue;
    
    private int maxNodes;

    /* Statistical information */
    private final RouteLengthChecker routeLengthChecker = new RouteLengthChecker();
    
    /**
     * @param server
     * @param localNode
     * @param params    The parameters to search for the content which we need to find
     * @param config
     */
    public ContentLookupOperation(MNode localNode, MContentKey lookupKey, int maxNodes)
    {
        /* Construct our lookup message */
        this.lookupMessage = new ContentLookupMessage(lookupKey);
        this.localNode = localNode;
        this.maxNodes = maxNodes;

		this.loopupQueue = new LookupQueue(lookupKey.getKey(), localNode.getCurrentConfiguration().k());
    }

    @Override
    public void execute() throws Throwable
    {    	
		/* Set the local node as already asked */
		loopupQueue.put(this.localNode.getNodeId(), LookupStatus.Asked);
        
        /* Also add the initial set of nodes to the routeLengthChecker */
		loopupQueue.addNodes(this.localNode.getRoutingTable().getAllNodes());

        /**
		 * If we haven't found the requested amount of content as yet, keey trying until
		 * config.operationTimeout() time has expired
		 */
		int totalTimeWaited = 0;
		int timeInterval = 10; // We re-check every n milliseconds
		while (totalTimeWaited < localNode.getCurrentConfiguration().operationTimeout()) {
			if (this.askNodesorFinish() || isLookupDone()) {
				break;
			} else {
				synchronized (this) {
					wait(timeInterval);
					totalTimeWaited += timeInterval;
				}
			}
		}
	}

    /**
     * Asks some of the K closest nodes seen but not yet queried.
     * Assures that no more than DefaultConfiguration.CONCURRENCY messages are in transit at a time
     *
     * This method should be called every time a reply is received or a timeout occurs.
     *
     * If all K closest nodes have been asked and there are no messages in transit,
     * the algorithm is finished.
     *
     * @return <code>true</code> if finished OR <code>false</code> otherwise
     * @throws Throwable 
     */
    private boolean askNodesorFinish()
    {
		int awaitNodeSize = loopupQueue.getAwatingNodes().size();
		
        /* If >= CONCURRENCY nodes are in transit, don't do anything */
        if (localNode.getCurrentConfiguration().maxConcurrentMessagesTransiting() <= awaitNodeSize)
        {
            return false;
        }

        /* Get unqueried nodes among the K closest seen that have not FAILED */
        List<MId> unasked = this.loopupQueue.closestNodesNotFailed(LookupStatus.UnAsked);

        if (unasked.isEmpty() && awaitNodeSize==0)
        {
            /* We have no unasked nodes nor any messages in transit, we're finished! */
            return true;
        }


        /**
         * Send messages to nodes in the list;
         * making sure than no more than CONCURRENCY messsages are in transit
         */
        for (int i = 0; (awaitNodeSize < localNode.getCurrentConfiguration().maxConcurrentMessagesTransiting()) && (i < unasked.size()); i++)
        {
        	MId n = (MId) unasked.get(i);

            try{
            	PeerLink link = localNode.getNetService().getPeerLink(n);
        		link.sendMessage(lookupMessage, this);
        		loopupQueue.put(n, LookupStatus.Awating);
            }catch(Exception e){
                /* Mark this node as failed and inform the routing table that it's unresponsive */
				loopupQueue.put(n, LookupStatus.Failed);
                localNode.getRoutingTable().setUnresponsiveContact(n);
            }
        }


        /* We're not finished as yet, return false */
        return false;
    }

    private boolean isLookupDone() {
    	return this.maxNodes<=nodesFound.size();
    }

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
        if (isLookupDone()){
            return;
        }

		synchronized (this) {

            /* Add the origin node to our routing table */
            this.localNode.getRoutingTable().insert(link.getRemoteMId());

            /* Set that we've completed ASKing the origin node */
			this.loopupQueue.put(link.getRemoteMId(), LookupStatus.Asked);

			if (incoming instanceof ContentLookupReplyMessage) {
//				ContentLookupReplyMessage msg = (ContentLookupReplyMessage) incoming;

				/* Add the origin node to our routing table */
				this.nodesFound.add(link.getRemoteMId());
			} else {
				/*
				 * The reply received is a NodeReplyMessage with nodes closest to the content
				 * needed
				 */
				NodeReplyMessage msg = (NodeReplyMessage) incoming;

				/* Add the received nodes to the routeLengthChecker */
				this.routeLengthChecker.addNodes(msg.getNodes(), link.getRemoteMId());

				/* Add the received nodes to our nodes list to query */
				this.loopupQueue.addNodes(msg.getNodes());
			}
			notify();
		}
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

		this.localNode.getRoutingTable().setUnresponsiveContact(n);
		
        /* Mark this node as failed and inform the routing table that it's unresponsive */
        synchronized (this) {
			this.loopupQueue.put(n, LookupStatus.Failed);
			notify();
		}
	}

    /**
     * @return How many hops it took in order to get to the content.
     */
    public int routeLength()
    {
        return this.routeLengthChecker.getRouteLength();
    }

    public List<MId> getNodesFound() {
		return nodesFound;
	}
}
