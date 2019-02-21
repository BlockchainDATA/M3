package eco.data.m3.routing.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.utp.congestion.LedbatBase;
import eco.data.m3.routing.MNode;

/**
 * An operation that handles refreshing the entire Kademlia Systems including buckets and content
 *
 * @author xquan
 * 
 */
public class RefreshOperation implements IOperation{

    private static final Logger logger =
        LoggerFactory.getLogger(LedbatBase.class.getName());
    
    private final MNode localNode;

    public RefreshOperation(MNode localNode)
    {
        this.localNode = localNode;
    }

    @Override
    public void execute() throws Throwable
    {
        /* Run our BucketRefreshOperation to refresh buckets */
        new BucketRefreshOperation(this.localNode).execute();

        /* After buckets have been refreshed, we refresh content */
        new ContentRefreshOperation(this.localNode).execute();
    }
}
