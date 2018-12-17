package eco.data.m3.routing.operation;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;

import java.io.IOException;
import java.util.HashMap;

/**
 * At each time interval t, nodes need to refresh their K-Buckets
 * This operation takes care of refreshing this node's K-Buckets
 *
 * @author xquan
 *
 */
public class BucketRefreshOperation implements IOperation{

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
     *
     * @throws java.io.IOException
     */
    @Override
    public synchronized void execute() throws IOException
    {
    	System.out.println("--------- All Nodes --------");
    	for (MId node : localNode.getRoutingTable().getAllNodes()) {
			System.out.println(node);
		};
    	System.out.println("---------   End     --------");
    	
    	int total = 0;
//    	HashMap<String, Integer> nodeMap = new HashMap<>();
    	
        for (int i = 1; i < MId.ID_LENGTH; i++)
        {
            /* Construct a NodeId that is i bits away from the current node Id */
            final MId current = (localNode.getNodeId()).generateNodeIdByDistance(i);
            
//            // when nodes are not very much , reduce message sent
//            if(nodeMap.containsKey(current.toString()))
//            	continue;
//            
//            nodeMap.put(current.toString(), i);
            // System.out.println("Bucket Refresh ID " + i + ", " + current+" : " + total++);

            /* Run the Node Lookup Operation, each in a different thread to speed up things */
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        new NodeLookupOperation(localNode, current).execute();
                    }
                    catch (IOException e)
                    {
                        //System.err.println("Bucket Refresh Operation Failed. Msg: " + e.getMessage());
                    } catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }.start();
        }
    }

}
