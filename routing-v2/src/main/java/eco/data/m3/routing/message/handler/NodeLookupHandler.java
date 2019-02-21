package eco.data.m3.routing.message.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;

/**
 * Receives a NodeLookupMessage and sends a NodeReplyMessage as reply with the K-Closest nodes to the ID sent.
 * 
 * @author xquan
 *
 */
public class NodeLookupHandler extends MessageHandler{

    private static final Logger logger =
        LoggerFactory.getLogger(NodeLookupHandler.class.getName());
	
	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
		
		// logger.debug("Node Lookup handle : " + incoming.getSrcConvId() );
        NodeLookupMessage msg = (NodeLookupMessage) incoming;

        MNode localNode = (MNode) link.getPeerNode();
        
//        logger.debug("" + link.getLocalMId() + ", " + link.getRemoteMId());
//        if(link.getRemoteMId()==null)
//        	return;
        
        /* Update the local space by inserting the origin node. */
        localNode.getRoutingTable().insert(link.getRemoteMId());

        /* Find nodes closest to the LookupId */
        List<MId> nodes = localNode.getRoutingTable().findClosest(msg.getLookupId(), localNode.getCurrentConfiguration().k());

        /* Respond to the NodeLookupMessage */
        Message reply = new NodeReplyMessage(nodes);
        reply.setDestConvId(incoming.getSrcConvId());
        link.sendMessage(reply, null);		
        // logger.debug("Node Lookup Reply : " + reply.getSrcConvId() );
	}

	@Override
	public void timeout(PeerLink link, Message msg) {
		
	}

}
