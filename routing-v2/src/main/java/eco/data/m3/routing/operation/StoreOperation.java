package eco.data.m3.routing.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.content.MContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.StoreInitReplyMessage;

/**
 * Operation that stores a DHT Content onto the closest nodes to the content Key
 * 
 * @author xquan
 *
 */
public class StoreOperation implements IOperation {

    private static final Logger logger =
        LoggerFactory.getLogger(StoreOperation.class.getName());
    
	private final MNode localNode;
	private final MContent content;
	private List<MId> nodesStoredAt;
	
	private HashMap<MId, Boolean> nodeStoreMap = new HashMap<>();

	public StoreOperation(MNode localNode, MContent content) {
		this.localNode = localNode;
		this.content = content;
	}

	@Override
	public void execute() throws Throwable {		
		/* Get the nodes on which we need to store the content */
		NodeLookupOperation ndlo = new NodeLookupOperation(this.localNode, this.content.getMeta().getKey());
		ndlo.execute();
		nodesStoredAt = ndlo.getClosestNodes();
		
		StoreToNodesOperation sop = new StoreToNodesOperation(localNode, content, nodesStoredAt);
		sop.execute();
	}

	public List<MId> getNodesStoredAt() {
		return nodesStoredAt;
	}
	
}
