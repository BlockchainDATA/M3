package eco.data.m3.routing.message.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.StoreInitMessage;
import eco.data.m3.routing.message.StoreInitReplyMessage;

/**
 * Receiver for incoming StoreContentMessage
 * 
 * @author xquan
 *
 */
public class StoreInitHandler extends MessageHandler{
	
    private static final Logger logger =
            LoggerFactory.getLogger(StoreInitHandler.class.getName());

	@Override
	public boolean shouldHandleInstantly() {
		return false;
	}
	
	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
        /* It's a StoreContentMessage we're receiving */
        StoreInitMessage msg = (StoreInitMessage) incoming;

        MNode localNode = (MNode) link.getPeerNode();        
        
        /* Insert the message sender into this node's routing table */
        localNode.getRoutingTable().insert(link.getRemoteMId());
        
        boolean needStore = localNode.getDHT().tryStore(msg.getMeta());
        short fd = 0;
        if(needStore) {
        	fd = localNode.getDHT().openFD(msg.getMeta());
        }
        
        StoreInitReplyMessage re = new StoreInitReplyMessage(needStore, fd);
        re.setDestConvId(incoming.getSrcConvId());
        link.sendMessage(re, null);
        
	}

	@Override
	public void timeout(PeerLink link, Message msg) {

	}

}
