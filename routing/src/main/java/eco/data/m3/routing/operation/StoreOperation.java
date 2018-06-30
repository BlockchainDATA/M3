package eco.data.m3.routing.operation;

import java.io.IOException;
import java.util.List;
import eco.data.m3.routing.KadConfiguration;
import eco.data.m3.routing.KadServer;
import eco.data.m3.routing.KademliaNode;
import eco.data.m3.routing.dht.JKademliaStorageEntry;
import eco.data.m3.routing.dht.KademliaDHT;
import eco.data.m3.routing.dht.KademliaStorageEntry;
import eco.data.m3.routing.message.Message;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.node.Node;

/**
 * Operation that stores a DHT Content onto the K closest nodes to the content Key
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class StoreOperation implements Operation
{

    private final KadServer server;
    private final KademliaNode localNode;
    private final JKademliaStorageEntry storageEntry;
    private final KademliaDHT localDht;
    private final KadConfiguration config;

    /**
     * @param server
     * @param localNode
     * @param storageEntry The content to be stored on the DHT
     * @param localDht     The local DHT
     * @param config
     */
    public StoreOperation(KadServer server, KademliaNode localNode, JKademliaStorageEntry storageEntry, KademliaDHT localDht, KadConfiguration config)
    {
        this.server = server;
        this.localNode = localNode;
        this.storageEntry = storageEntry;
        this.localDht = localDht;
        this.config = config;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        /* Get the nodes on which we need to store the content */
        NodeLookupOperation ndlo = new NodeLookupOperation(this.server, this.localNode, this.storageEntry.getContentMetadata().getKey(), this.config);
        ndlo.execute();
        List<Node> nodes = ndlo.getClosestNodes();

        /* Create the message */
        Message msg = new StoreContentMessage(this.localNode.getNode(), this.storageEntry);

        /*Store the message on all of the K-Nodes*/
        for (Node n : nodes)
        {
            if (n.equals(this.localNode.getNode()))
            {
                /* Store the content locally */
                this.localDht.store(this.storageEntry);
            }
            else
            {
                /**
                 * @todo Create a receiver that receives a store acknowledgement message to count how many nodes a content have been stored at
                 */
                this.server.sendMessage(n, msg, null);
            }
        }
    }

    /**
     * @return The number of nodes that have stored this content
     *
     * @todo Implement this method
     */
    public int numNodesStoredAt()
    {
        return 1;
    }
}
