package eco.data.m3.routing.operation;

import java.io.IOException;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.StorageEntryMetadata;
import eco.data.m3.routing.exception.ContentNotFoundException;
import eco.data.m3.routing.message.StoreContentMessage;

public class ContentRefreshOperation implements IOperation{

    private final MNode localNode;

    public ContentRefreshOperation(MNode localNode)
    {
        this.localNode = localNode;
    }

    /**
     * For each content stored on this DHT, distribute it to the K closest nodes
 Also delete the content if this node is no longer one of the K closest nodes

 We assume that our JKademliaRoutingTable is updated, and we can get the K closest nodes from that table
     *
     * @throws java.io.IOException
     */
    @Override
    public void execute() throws IOException
    {
        /* Get a list of all storage entries for content */
        List<StorageEntryMetadata> entries = localNode.getDHT().getStorageEntries();

        /* If a content was last republished before this time, then we need to republish it */
        final long minRepublishTime = (System.currentTimeMillis() / 1000L) - localNode.getCurrentConfiguration().restoreInterval();

        /* For each storage entry, distribute it */
        for (StorageEntryMetadata e : entries)
        {
            /* Check last update time of this entry and only distribute it if it has been last updated > 1 hour ago */
            if (e.lastRepublished() > minRepublishTime)
            {
                continue;
            }

            /* Set that this content is now republished */
            e.updateLastRepublished();

            /* Get the K closest nodes to this entries */
            List<MId> closestNodes = this.localNode.getRoutingTable().findClosest(e.getKey(), localNode.getCurrentConfiguration().k());

            /* Create the message */
            Message msg = new StoreContentMessage(this.localNode.getNodeId(), localNode.getDHT().get(e));

            /*Store the message on all of the K-Nodes*/
            for (MId n : closestNodes)
            {
                /*We don't need to again store the content locally, it's already here*/
                if (!n.equals(this.localNode.getNodeId()))
                {
                    /* Send a contentstore operation to the K-Closest nodes */
                	localNode.getServer().sendMessage(n, msg, null);
                }
            }

            /* Delete any content on this node that this node is not one of the K-Closest nodes to */
            try
            {
                if (!closestNodes.contains(this.localNode.getNodeId()))
                {
                	localNode.getDHT().remove(e);
                }
            }
            catch (ContentNotFoundException cnfe)
            {
                /* It would be weird if the content is not found here */
                System.err.println("ContentRefreshOperation: Removing content from local node, content not found... Message: " + cnfe.getMessage());
            }
        }

    }

}
