package eco.data.m3.routing.operation;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import eco.data.m3.net.core.KeyComparator;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.exception.RoutingException;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.exception.ContentNotFoundException;
import eco.data.m3.routing.exception.UnknownMessageException;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentMessage;
import eco.data.m3.routing.message.NodeReplyMessage;
import eco.data.m3.routing.util.RouteLengthChecker;

public class ContentLookupOperation extends MessageHandler implements IOperation{


    /* Constants */
    private static final Byte UNASKED = (byte) 0x00;
    private static final Byte AWAITING = (byte) 0x01;
    private static final Byte ASKED = (byte) 0x02;
    private static final Byte FAILED = (byte) 0x03;

    private final MNode localNode;
    private StorageEntry contentFound = null;

    private final ContentLookupMessage lookupMessage;

    private boolean isContentFound;
    private final SortedMap<MId, Byte> nodes;

    /* Tracks messages in transit and awaiting reply */
    private final Map<Integer, MId> messagesTransiting;

    /* Used to sort nodes */
    private final Comparator comparator;

    /* Statistical information */
    private final RouteLengthChecker routeLengthChecker;

    
    {
        messagesTransiting = new HashMap<>();
        isContentFound = false;
        routeLengthChecker = new RouteLengthChecker();
    }

    /**
     * @param server
     * @param localNode
     * @param params    The parameters to search for the content which we need to find
     * @param config
     */
    public ContentLookupOperation(MNode localNode, GetParameter params)
    {
    	super(localNode.getServer());
    	
        /* Construct our lookup message */
        this.lookupMessage = new ContentLookupMessage(localNode.getNodeId(), params);

        this.localNode = localNode;

        /**
         * We initialize a TreeMap to store nodes.
         * This map will be sorted by which nodes are closest to the lookupId
         */
        this.comparator = new KeyComparator(params.getKey());
        this.nodes = new TreeMap(this.comparator);
    }

    /**
     * @throws java.io.IOException
     * @throws kademlia.exceptions.RoutingException
     */
    @Override
    public synchronized void execute() throws IOException, RoutingException
    {
        try
        {
            /* Set the local node as already asked */
            nodes.put(this.localNode.getNodeId(), ASKED);

            /**
             * We add all nodes here instead of the K-Closest because there may be the case that the K-Closest are offline
             * - The operation takes care of looking at the K-Closest.
             */
            List<MId> allNodes = this.localNode.getRoutingTable().getAllNodes();
            this.addNodes(allNodes);
            
            /* Also add the initial set of nodes to the routeLengthChecker */
            this.routeLengthChecker.addInitialNodes(allNodes);

            /**
             * If we haven't found the requested amount of content as yet,
             * keey trying until config.operationTimeout() time has expired
             */
            int totalTimeWaited = 0;
            int timeInterval = 10;     // We re-check every n milliseconds
            while (totalTimeWaited < localNode.getCurrentConfiguration().operationTimeout())
            {
                if (!this.askNodesorFinish() && !isContentFound)
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
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
     */
    private boolean askNodesorFinish() throws IOException
    {
        /* If >= CONCURRENCY nodes are in transit, don't do anything */
        if (localNode.getCurrentConfiguration().maxConcurrentMessagesTransiting() <= this.messagesTransiting.size())
        {
            return false;
        }

        /* Get unqueried nodes among the K closest seen that have not FAILED */
        List<MId> unasked = this.closestNodesNotFailed(UNASKED);

        if (unasked.isEmpty() && this.messagesTransiting.isEmpty())
        {
            /* We have no unasked nodes nor any messages in transit, we're finished! */
            return true;
        }

        /* Sort nodes according to criteria */
        Collections.sort(unasked, this.comparator);

        /**
         * Send messages to nodes in the list;
         * making sure than no more than CONCURRENCY messsages are in transit
         */
        for (int i = 0; (this.messagesTransiting.size() < localNode.getCurrentConfiguration().maxConcurrentMessagesTransiting()) && (i < unasked.size()); i++)
        {
        	MId n = (MId) unasked.get(i);

            int comm = server.sendMessage(n, lookupMessage, this);

            this.nodes.put(n, AWAITING);
            this.messagesTransiting.put(comm, n);
        }

        /* We're not finished as yet, return false */
        return false;
    }

    /**
     * Find The K closest nodes to the target lookupId given that have not FAILED.
     * From those K, get those that have the specified status
     *
     * @param status The status of the nodes to return
     *
     * @return A List of the closest nodes
     */
    private List<MId> closestNodesNotFailed(Byte status)
    {
        List<MId> closestNodes = new ArrayList<>(localNode.getCurrentConfiguration().k());
        int remainingSpaces = localNode.getCurrentConfiguration().k();

        for (Map.Entry e : this.nodes.entrySet())
        {
            if (!FAILED.equals(e.getValue()))
            {
                if (status.equals(e.getValue()))
                {
                    /* We got one with the required status, now add it */
                    closestNodes.add((MId) e.getKey());
                }

                if (--remainingSpaces == 0)
                {
                    break;
                }
            }
        }

        return closestNodes;
    }

    @Override
    public synchronized void receive(Message incoming, int comm) throws IOException, RoutingException
    {
        if (this.isContentFound)
        {
            return;
        }

        if (incoming instanceof ContentMessage)
        {
            /* The reply received is a content message with the required content, take it in */
            ContentMessage msg = (ContentMessage) incoming;

            /* Add the origin node to our routing table */
            this.localNode.getRoutingTable().insert(msg.getOrigin());

            /* Get the Content and check if it satisfies the required parameters */
            StorageEntry content = msg.getContent();
            this.contentFound = content;
            this.isContentFound = true;
        }
        else
        {
            /* The reply received is a NodeReplyMessage with nodes closest to the content needed */
            NodeReplyMessage msg = (NodeReplyMessage) incoming;

            /* Add the origin node to our routing table */
            MId origin = msg.getOrigin();
            this.localNode.getRoutingTable().insert(origin);

            /* Set that we've completed ASKing the origin node */
            this.nodes.put(origin, ASKED);

            /* Remove this msg from messagesTransiting since it's completed now */
            this.messagesTransiting.remove(comm);
            
            /* Add the received nodes to the routeLengthChecker */
            this.routeLengthChecker.addNodes(msg.getNodes(), origin);

            /* Add the received nodes to our nodes list to query */
            this.addNodes(msg.getNodes());
            this.askNodesorFinish();
        }
    }

    /**
     * A node does not respond or a packet was lost, we set this node as failed
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public synchronized void timeout(int comm) throws IOException
    {
        /* Get the node associated with this communication */
    	MId n = this.messagesTransiting.get(new Integer(comm));

        if (n == null)
        {
            throw new UnknownMessageException("Unknown comm: " + comm);
        }

        /* Mark this node as failed and inform the routing table that it's unresponsive */
        this.nodes.put(n, FAILED);
        this.localNode.getRoutingTable().setUnresponsiveContact(n);
        this.messagesTransiting.remove(comm);

        this.askNodesorFinish();
    }
    
    /**
     * @return Whether the content was found or not.
     */
    public boolean isContentFound()
    {
        return this.isContentFound;
    }

    /**
     * @return The list of all content found during the lookup operation
     *
     * @throws kademlia.exceptions.ContentNotFoundException
     */
    public StorageEntry getContentFound() throws ContentNotFoundException
    {
        if (this.isContentFound)
        {
            return this.contentFound;
        }
        else
        {
            throw new ContentNotFoundException("No Value was found for the given key.");
        }
    }

    /**
     * @return How many hops it took in order to get to the content.
     */
    public int routeLength()
    {
        return this.routeLengthChecker.getRouteLength();
    }
}
