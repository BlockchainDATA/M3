package eco.data.m3.routing.message.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.StoreContentMessage;

/**
 * Receiver for incoming StoreContentMessage
 * 
 * @author xquan
 *
 */
public class StoreContentHandler extends MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(StoreContentHandler.class.getName());

	@Override
	public boolean shouldHandleInstantly() {
		return false;
	}

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
		/* It's a StoreContentMessage we're receiving */
		StoreContentMessage msg = (StoreContentMessage) incoming;

		MNode localNode = (MNode) link.getPeerNode();

		/* Insert the message sender into this node's routing table */
		localNode.getRoutingTable().insert(link.getRemoteMId());

		// logger.debug("Store Content : " + msg.getContent());
		localNode.getDHT().writeData(msg.getFd(), msg.getStart(), msg.getContent());

		// Try to close the file when data is done
		if (msg.isEndOfFile()) {
			localNode.getDHT().finishStore(msg.getFd());
			localNode.getDHT().closeFD(msg.getFd());
		}
	}

	@Override
	public void timeout(PeerLink link, Message msg) {

	}

}
