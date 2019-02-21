package eco.data.m3.routing.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;

/**
 * At each time interval t, nodes need to refresh their K-Buckets
 * This operation takes care of refreshing this node's K-Buckets
 *
 * @author xquan
 *
 */
public class BucketRefreshOperation implements IOperation{

    private static final Logger logger =
        LoggerFactory.getLogger(BucketRefreshOperation.class.getName());

    private final MNode localNode;

    public BucketRefreshOperation(MNode localNode)
    {
        this.localNode = localNode;
    }

    /**
     * Each bucket need to be refreshed at every time interval t.
     * Find an identifier in each bucket's range, use it to look for nodes closest to this identifier
     * allowing the bucket to be refreshed.
     *
     * Then Do a NodeLookupOperation for each of the generated NodeIds,
     * This will find the K-Closest nodes to that ID, and update the necessary K-Bucket
     * @throws Throwable 
     */
    @Override
    public void execute() throws Throwable
    {
    	logger.debug("--------- Bucket Refresh " + localNode.getNodeId() +"--------");
    	for (MId node : localNode.getRoutingTable().getAllNodes()) {
    		logger.debug(" - " + node.toString());
		};
    	
//    	HashMap<String, Integer> nodeMap = new HashMap<>();
    	
        for (int i = 1; i < MId.ID_LENGTH; i++)
        {
            /* Construct a NodeId that is i bits away from the current node Id */
            final MId current = (localNode.getNodeId()).generateNodeIdByDistance(i);

// System.nanoTime();
            /* Run the Node Lookup Operation, each in a different thread to speed up things */
            new NodeLookupOperation(localNode, current).execute();
            // System.nanoTime();
            
//            localNode.getLookupEventLoop().execute(new Runnable() {
//				@Override
//				public void run() {
//                    try
//                    {
//                    }
//                    catch (IOException e)
//                    {
//                        //System.err.println("Bucket Refresh Operation Failed. Msg: " + e.getMessage());
//                    } catch (Throwable e) {
//						e.printStackTrace();
//					}
//				}
//			});
        }
		logger.debug("---------  Bucket Refresh End  --------");
    }

}
