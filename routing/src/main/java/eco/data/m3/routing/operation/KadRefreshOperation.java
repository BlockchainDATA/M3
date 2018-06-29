package eco.data.m3.routing.operation;

import java.io.IOException;
import eco.data.m3.routing.KadConfiguration;
import eco.data.m3.routing.KadServer;
import eco.data.m3.routing.KademliaNode;
import eco.data.m3.routing.dht.KademliaDHT;

/**
 * An operation that handles refreshing the entire Kademlia Systems including buckets and content
 *
 * @author Joshua Kissoon
 * @since 20140306
 */
public class KadRefreshOperation implements Operation
{

    private final KadServer server;
    private final KademliaNode localNode;
    private final KademliaDHT dht;
    private final KadConfiguration config;

    public KadRefreshOperation(KadServer server, KademliaNode localNode, KademliaDHT dht, KadConfiguration config)
    {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
        this.config = config;
    }

    @Override
    public void execute() throws IOException
    {
        /* Run our BucketRefreshOperation to refresh buckets */
        new BucketRefreshOperation(this.server, this.localNode, this.config).execute();

        /* After buckets have been refreshed, we refresh content */
        new ContentRefreshOperation(this.server, this.localNode, this.dht, this.config).execute();
    }
}
