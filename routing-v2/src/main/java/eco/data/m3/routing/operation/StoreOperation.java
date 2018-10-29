package eco.data.m3.routing.operation;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.StoreContentReplyMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Operation that stores a DHT Content onto the closest nodes to the content Key
 * 
 * @author xquan
 *
 */
public class StoreOperation extends MessageHandler implements IOperation{

    private final MNode localNode;
    private final StorageEntry storageEntry;
    private List<MId> nodesStoredAt;
    
    private int id;
    private static int counter = 0;
    private int repeatTimes = 5;
    
    private HashMap<String, Byte> replyNodeMap = new HashMap<>();

    public StoreOperation(MNode localNode, StorageEntry storageEntry)
    {
    	super(localNode.getServer());
        this.localNode = localNode;
        this.storageEntry = storageEntry;
        this.id = counter++;
        this.setResponseTimeout(localNode.getCurrentConfiguration().storeReplyTimeout());
    }
    
	@Override
	public synchronized void execute() throws Throwable {
        /* Get the nodes on which we need to store the content */
        NodeLookupOperation ndlo = new NodeLookupOperation(this.localNode, this.storageEntry.getContentMetadata().getKey());
        ndlo.execute();
        List<MId> nodes = ndlo.getClosestNodes();

        /* Create the message */
        Message msg = new StoreContentMessage(this.localNode.getNodeId(), this.storageEntry);
        
        int time = 0;
        
        while(time<repeatTimes && replyNodeMap.size()<nodes.size()) {
	        /*Store the message on all of the K-Nodes*/
	        for (MId n : nodes)
	        {
	        	if(replyNodeMap.containsKey(n.toString()))
	        		continue;
	        	System.out.println("Store Data " + id + " to " + n + "  at : " + time);
	            if (n.equals(this.localNode.getNodeId()))
	            {
	                /* Store the content locally */
	            	localNode.getDHT().store(this.storageEntry);
	            	replyNodeMap.put(n.toString(), (byte) 0);
	            }
	            else
	            {
	            	localNode.getServer().sendMessage(n, msg, this);
	            }
	        }
	        
            int totalTimeWaited = 0;
            int timeInterval = 50;     // We re-check every 300 milliseconds
            while (totalTimeWaited < getResponseTimeout())
            {
                if (replyNodeMap.size()<nodes.size())
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
            time++;
        }
        nodesStoredAt = new ArrayList<>();
        for (String murl : replyNodeMap.keySet()) {
        	MId m = MId.fromHexString(murl);
        	byte code = replyNodeMap.get(m);
        	if(code == 0) // code success
        		nodesStoredAt.add(m);
		}
	}
    
    public List<MId> getNodesStoredAt()
    {
    	return nodesStoredAt;
    }

	@Override
	public void receive(Message incoming, int conversationId) throws Throwable {
		StoreContentReplyMessage replyMessage = (StoreContentReplyMessage) incoming;
		String origin = incoming.getOrigin().toString();
		
		if(!replyNodeMap.containsKey(origin) || replyNodeMap.get(origin)!=0)
			replyNodeMap.put(incoming.getOrigin().toString(), replyMessage.getCode());
		
	}

	@Override
	public void timeout(int conversationId) throws Throwable {
//		System.out.println(conversationId);
	}
}
