package eco.data.m3.routing.operation;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.content.MContentKey;
import eco.data.m3.content.MContentTransferListener;
import eco.data.m3.content.exception.ContentNotFoundException;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.ContentRetrieveMessage;
import eco.data.m3.routing.message.ContentRetrieveReplyMessage;

/**
 * Operation that stores a DHT Content onto the closest nodes to the content Key
 * 
 * @author xquan
 *
 */
public class ContentRetrieveOperation extends MessageHandler implements IOperation, MContentTransferListener{

    private static final Logger logger =
        LoggerFactory.getLogger(ContentRetrieveOperation.class.getName());
    
	private final MNode localNode;
	private final MContentKey contentKey;
	private final MId remoteNode;
	private boolean transferDone = false;
	
	private boolean hasContent;
	private final long RETRIEVE_TIME_OUT = 30000;
	
	private HashMap<MId, Boolean> nodeStoreMap = new HashMap<>();

	public ContentRetrieveOperation(MNode localNode, MContentKey contentKey, MId remoteNode) {
		this.localNode = localNode;
		this.contentKey = contentKey;
		this.remoteNode = remoteNode;
	}

	@Override
	public void execute() throws Throwable {		
		boolean needStore = localNode.getDHT().tryStore(contentKey);
		if(!needStore)
			return;
		
		ContentRetrieveMessage msg = new ContentRetrieveMessage(contentKey);
		PeerLink link = localNode.getNetService().getPeerLink(remoteNode);
		if(link==null)
			throw new ContentNotFoundException("Link is unreachable for Content");
		
		link.sendMessage(msg, this, RETRIEVE_TIME_OUT);
		
		synchronized (this) {
			wait();
		}

		if(!hasContent) {
			throw new ContentNotFoundException("Not Found Content");
		}
		
		if(localNode.getDHT().contains(contentKey))
			return;
		
		localNode.getDHT().getContentManger().addTransferListener(contentKey, this);
		while(!transferDone) {
			synchronized (this) {
				logger.debug("Wait Content Transfering");
				wait();				
			}
		}
	}

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {
		ContentRetrieveReplyMessage msg = (ContentRetrieveReplyMessage) incoming;	
		synchronized (this) {	
			this.hasContent = msg.isContentReady();
			notify();
		}
	}

	@Override
	public void timeout(PeerLink link, Message msg) {
		synchronized (this) {
			this.hasContent = false;
			notify();
		}
	}

	@Override
	public void transferBegin(MContentKey contentKey) {
		logger.debug("Tranfer Begin");
	}

	@Override
	public void transferProgress(MContentKey contentKey, double percent) {		
		logger.debug("Tranfer Progress : " + percent);
		
	}

	@Override
	public void transferEnd(MContentKey contentKey) {
		logger.debug("Tranfer End");
		synchronized (this) {
			transferDone = true;
			notify();
		}
	}

	@Override
	public void transferFailed(MContentKey contentKey) {
		logger.debug("Tranfer Failed");
		synchronized (this) {
			transferDone = true;
			notify();
		}		
	}

}
