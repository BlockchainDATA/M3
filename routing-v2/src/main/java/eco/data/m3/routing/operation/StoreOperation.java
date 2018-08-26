package eco.data.m3.routing.operation;

import java.io.IOException;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.Configuration;
import eco.data.m3.routing.core.DHT;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.message.StoreContentMessage;

/**
 * Operation that stores a DHT Content onto the closest nodes to the content Key
 * 
 * @author xquan
 *
 */
public class StoreOperation implements IOperation{

    private final MNode localNode;
    private final StorageEntry storageEntry;

    public StoreOperation(MNode localNode, StorageEntry storageEntry)
    {
        this.localNode = localNode;
        this.storageEntry = storageEntry;
    }
    
	@Override
	public synchronized void execute() throws IOException {
        /* Get the nodes on which we need to store the content */
        NodeLookupOperation ndlo = new NodeLookupOperation(this.localNode, this.storageEntry.getContentMetadata().getKey());
        ndlo.execute();
        List<MId> nodes = ndlo.getClosestNodes();

        /* Create the message */
        Message msg = new StoreContentMessage(this.localNode.getNodeId(), this.storageEntry);

        /*Store the message on all of the K-Nodes*/
        for (MId n : nodes)
        {
            if (n.equals(this.localNode.getNodeId()))
            {
                /* Store the content locally */
            	localNode.getDHT().store(this.storageEntry);
            }
            else
            {
                /**
                 * @todo Create a receiver that receives a store acknowledgement message to count how many nodes a content have been stored at
                 */
            	localNode.getServer().sendMessage(n, msg, null);
            }
        }
		
	}

    /**
     * @return The number of nodes that have stored this content
     *
     */
    public int numNodesStoredAt()
    {
        return 1;
    }
}
