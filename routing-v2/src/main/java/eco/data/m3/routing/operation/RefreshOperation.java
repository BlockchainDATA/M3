package eco.data.m3.routing.operation;

import java.io.IOException;

import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.Configuration;
import eco.data.m3.routing.core.DHT;

/**
 * An operation that handles refreshing the entire Kademlia Systems including buckets and content
 *
 * @author xquan
 * 
 */
public class RefreshOperation implements IOperation{

    private final MNode localNode;

    public RefreshOperation(MNode localNode)
    {
        this.localNode = localNode;
    }

    @Override
    public void execute() throws IOException
    {
        /* Run our BucketRefreshOperation to refresh buckets */
        new BucketRefreshOperation(this.localNode).execute();

        /* After buckets have been refreshed, we refresh content */
        new ContentRefreshOperation(this.localNode).execute();
    }
}
