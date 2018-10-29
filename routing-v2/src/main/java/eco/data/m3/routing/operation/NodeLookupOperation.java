package eco.data.m3.routing.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eco.data.m3.net.core.KeyComparator;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;

/**
 * Finds the K closest nodes to a specified identifier
 * The algorithm terminates when it has gotten responses from the K closest nodes it has seen.
 * Nodes that fail to respond are removed from consideration
 * 
 * @author xquan
 *
 */
public class NodeLookupOperation extends MessageHandler implements IOperation{

    /* Constants */
    private static final String UNASKED = "UnAsked";
    private static final String AWAITING = "Awaiting";
    private static final String ASKED = "Asked";
    private static final String FAILED = "Failed";

    private final MNode localNode;
    private final MConfiguration config;

    private final Message lookupMessage;        // Message sent to each peer
    private final Map<MId, String> nodes;

    /* Tracks messages in transit and awaiting reply */
    private final Map<Integer, MId> messagesTransiting = new HashMap<>();

    /* Used to sort nodes */
    private final Comparator comparator;

    /**
     * @param server    KadServer used for communication
     * @param localNode The local node making the communication
     * @param lookupId  The ID for which to find nodes close to
     * @param config
     */
    public NodeLookupOperation(MNode localNode, MId lookupId)
    {
    	super(localNode.getServer());
    	
        this.localNode = localNode;
        this.config = localNode.getCurrentConfiguration();

        this.lookupMessage = new NodeLookupMessage(localNode.getNodeId(), lookupId);

        /**
         * We initialize a TreeMap to store nodes.
         * This map will be sorted by which nodes are closest to the lookupId
         */
        this.comparator = new KeyComparator(lookupId);
        this.nodes = new TreeMap(this.comparator);
    }

    /**
     * @throws Throwable 
     */
    @Override
    public synchronized void execute() throws Throwable
    {
        try
        {
            /* Set the local node as already asked */
            nodes.put(this.localNode.getNodeId(), ASKED);

            /**
             * We add all nodes here instead of the K-Closest because there may be the case that the K-Closest are offline
             * - The operation takes care of looking at the K-Closest.
             */
            this.addNodes(this.localNode.getRoutingTable().getAllNodes());

            /* If we haven't finished as yet, wait for a maximum of config.operationTimeout() time */
            int totalTimeWaited = 0;
            int timeInterval = 10;     // We re-check every n milliseconds
            while (totalTimeWaited < this.config.operationTimeout())
            {
                if (!this.askNodesorFinish())
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }

            /* Now after we've finished, we would have an idea of offline nodes, lets update our routing table */
            this.localNode.getRoutingTable().setUnresponsiveContacts(this.getFailedNodes());

        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<MId> getClosestNodes()
    {
        return this.closestNodes(ASKED);
    }

    /**
     * Add nodes from this list to the set of nodes to lookup
     *
     * @param list The list from which to add nodes
     */
    public void addNodes(List<MId> list)
    {
        for (MId o : list)
        {
            /* If this node is not in the list, add the node */
            if (!nodes.containsKey(o))
            {
                nodes.put(o, UNASKED);
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
    private boolean askNodesorFinish() throws Throwable
    {
        /* If >= CONCURRENCY nodes are in transit, don't do anything */
        if (this.config.maxConcurrentMessagesTransiting() <= this.messagesTransiting.size())
        {
            return false;
        }

        /* Get unqueried nodes among the K closest seen that have not FAILED */
        List<MId> unasked = this.closestNodesNotFailed(UNASKED);
//        
//        System.out.println("UnAsked :");
//        for (MId mId : unasked) {
//			System.out.println(mId);
//		}

        if (unasked.isEmpty() && this.messagesTransiting.isEmpty())
        {
            /* We have no unasked nodes nor any messages in transit, we're finished! */
            return true;
        }

        /**
         * Send messages to nodes in the list;
         * making sure than no more than CONCURRENCY messsages are in transit
         */
        for (int i = 0; (this.messagesTransiting.size() < this.config.maxConcurrentMessagesTransiting()) && (i < unasked.size()); i++)
        {
        	MId n = (MId) unasked.get(i);

        	try {
	            int comm = server.sendMessage(n, lookupMessage, this);
	
	            this.nodes.put(n, AWAITING);
	            this.messagesTransiting.put(comm, n);
        	}catch (Exception e) {

//    			System.out.println(" -- Failed +" + n );
                /* Mark this node as failed and inform the routing table that it is unresponsive */
                this.nodes.put(n, FAILED);
                this.localNode.getRoutingTable().setUnresponsiveContact(n);
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

    /**
     * @param status The status of the nodes to return
     *
     * @return The K closest nodes to the target lookupId given that have the specified status
     */
    private List<MId> closestNodes(String status)
    {
        List<MId> closestNodes = new ArrayList<>(this.config.k());
        int remainingSpaces = this.config.k();

        for (Map.Entry e : this.nodes.entrySet())
        {
            if (status.equals(e.getValue()))
            {
                /* We got one with the required status, now add it */
                closestNodes.add((MId) e.getKey());
                if (--remainingSpaces == 0)
                {
                    break;
                }
            }
        }

        return closestNodes;
    }

    /**
     * Find The K closest nodes to the target lookupId given that have not FAILED.
     * From those K, get those that have the specified status
     *
     * @param status The status of the nodes to return
     *
     * @return A List of the closest nodes
     */
    private List<MId> closestNodesNotFailed(String status)
    {
        List<MId> closestNodes = new ArrayList<>(this.config.k());
        int remainingSpaces = this.config.k();

        for (Map.Entry<MId, String> e : this.nodes.entrySet())
        {
            if (!FAILED.equals(e.getValue()))
            {
                if (status.equals(e.getValue()))
                {
                    /* We got one with the required status, now add it */
                    closestNodes.add(e.getKey());
                }

                if (--remainingSpaces == 0)
                {
                    break;
                }
            }
        }

        return closestNodes;
    }

    /**
     * Receive and handle the incoming NodeReplyMessage
     *
     * @param comm
     * @throws Throwable 
     */
    @Override
    public synchronized void receive(Message incoming, int comm) throws Throwable
    {
        if (!(incoming instanceof NodeReplyMessage))
        {
            /* Not sure why we get a message of a different type here... @todo Figure it out. */
            return;
        }
        /* We receive a NodeReplyMessage with a set of nodes, read this message */
        NodeReplyMessage msg = (NodeReplyMessage) incoming;

        /* Add the origin node to our routing table */
        MId origin = msg.getOrigin();
        this.localNode.getRoutingTable().insert(origin);

        /* Set that we've completed ASKing the origin node */
        this.nodes.put(origin, ASKED);

        /* Remove this msg from messagesTransiting since it's completed now */
        this.messagesTransiting.remove(comm);

        /* Add the received nodes to our nodes list to query */
        this.addNodes(msg.getNodes());
        this.askNodesorFinish();
    }

    /**
     * A node does not respond or a packet was lost, we set this node as failed
     *
     * @param comm
     * @throws Throwable 
     */
    @Override
    public synchronized void timeout(int comm) throws Throwable
    {
        /* Get the node associated with this communication */
    	MId n = this.messagesTransiting.get(comm);

        if (n == null)
        {
            return;
        }

        /* Mark this node as failed and inform the routing table that it is unresponsive */
        this.nodes.put(n, FAILED);
        this.localNode.getRoutingTable().setUnresponsiveContact(n);
        this.messagesTransiting.remove(comm);

        this.askNodesorFinish();
    }

    public List<MId> getFailedNodes()
    {
        List<MId> failedNodes = new ArrayList<>();

        for (Map.Entry<MId, String> e : this.nodes.entrySet())
        {
            if (e.getValue().equals(FAILED))
            {
                failedNodes.add(e.getKey());
            }
        }

        return failedNodes;
    }

}
