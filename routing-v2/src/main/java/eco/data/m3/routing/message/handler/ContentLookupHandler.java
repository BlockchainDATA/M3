package eco.data.m3.routing.message.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentLookupReplyMessage;
import eco.data.m3.routing.message.NodeLookupMessage;

/**
 * Responds to a ContentLookupMessage by sending a ContentMessage containing the requested content;
 * if the requested content is not found, a NodeReplyMessage containing the K closest nodes to the request key is sent.
 * 
 * @author xquan
 *
 */
public class ContentLookupHandler extends MessageHandler{
	
    private static final Logger logger =
            LoggerFactory.getLogger(ContentLookupHandler.class.getName());

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
        ContentLookupMessage msg = (ContentLookupMessage) incoming;
        MNode localNode = (MNode) link.getPeerNode();
        localNode.getRoutingTable().insert(link.getRemoteMId());

        /* Check if we can have this data */
        if (localNode.getDHT().contains(msg.getParam()))
        {
            logger.debug("Found Data");
            
            /* Return a ContentMessage with the required data */
            ContentLookupReplyMessage cMsg = new ContentLookupReplyMessage(true);
            cMsg.setDestConvId(incoming.getSrcConvId());
            link.sendMessage(cMsg, null);

        }
        else
        {
            /**
             * Return a the K closest nodes to this content identifier
             * We create a NodeLookupReceiver and let this receiver handle this operation
             */
            NodeLookupMessage lkpMsg = new NodeLookupMessage(msg.getParam().getKey());
            new NodeLookupHandler().handle(link, lkpMsg);
        }
        
	}

	@Override
	public void timeout(PeerLink link, Message msg) {
		
	}

}
