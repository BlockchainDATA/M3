package eco.data.m3.routing.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.exception.RoutingException;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;

public class ConnectOperation implements IOperation{

    private static final Logger logger =
        LoggerFactory.getLogger(ConnectOperation.class.getName());

    private final MNode localNode;
    private final MId bootstrapNode;

    public ConnectOperation(MNode local, MId bootstrap)
    {
        this.localNode = local;
        this.bootstrapNode = bootstrap;
    }

    @Override
    public void execute() throws Throwable
    {
        try
        {
        	logger.debug("Connect from " + localNode.getNodeId() +" to : " + bootstrapNode);
        	
            /* Contact the bootstrap node */            
            PeerLink link = localNode.getNetService().getPeerLink(bootstrapNode);
            
            if (link == null){
                /* If we still haven't received any responses by then, do a routing timeout */
                throw new RoutingException("Connect Failed: " + localNode.getNodeId() +" to : " + bootstrapNode);
            }
            
            /* The bootstrap node has responded, insert it into our space */
            this.localNode.getRoutingTable().insert(this.bootstrapNode);

            /* Perform lookup for our own ID to get nodes close to us */
            IOperation lookup = new NodeLookupOperation(this.localNode, this.localNode.getNodeId());
            lookup.execute();

            /**
             * Refresh buckets to get a good routing table
             * After the above lookup operation, K nodes will be in our routing table,
             * Now we try to populate all of our buckets.
             */
            
            localNode.getEventLoop().execute(new Runnable() {				
				@Override
				public void run() {
		            try {
						new BucketRefreshOperation(localNode).execute();
					} catch (Throwable e) {
						e.printStackTrace();
					}					
				}
			});
        }
        catch (InterruptedException e)
        {
            System.err.println("Connect operation was interrupted. ");
        }
    }

}
