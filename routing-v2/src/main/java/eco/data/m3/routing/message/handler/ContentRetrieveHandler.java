package eco.data.m3.routing.message.handler;

import data.eco.net.p2p.channel.PeerLink;
import data.eco.net.p2p.message.Message;
import data.eco.net.p2p.message.MessageHandler;
import eco.data.m3.content.MContent;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.ContentRetrieveMessage;
import eco.data.m3.routing.message.ContentRetrieveReplyMessage;
import eco.data.m3.routing.operation.StoreToNodesOperation;

/**
 * Responds to a ContentLookupMessage by sending a ContentMessage containing the requested content;
 * if the requested content is not found, a NodeReplyMessage containing the K closest nodes to the request key is sent.
 * 
 * @author xquan
 *
 */
public class ContentRetrieveHandler extends MessageHandler{

	@Override
	public void handle(PeerLink link, Message incoming) throws Throwable {

        ContentRetrieveMessage msg = (ContentRetrieveMessage) incoming;
        MNode localNode = (MNode) link.getPeerNode();
        localNode.getRoutingTable().insert(link.getRemoteMId());

        /* Check if we can have this data */
        if (localNode.getDHT().contains(msg.getContentKey()))
        {
        	ContentRetrieveReplyMessage re = new ContentRetrieveReplyMessage( true );
        	re.setDestConvId(incoming.getSrcConvId());
        	link.sendMessage(re, null);
        	
        	localNode.getEventLoop().execute(new Runnable() {
				@Override
				public void run() {
		        	try {
			        	MContent content = localNode.getDHT().getContent(msg.getContentKey());
			        	StoreToNodesOperation sop = new StoreToNodesOperation(localNode, content, link.getRemoteMId());
						sop.execute();
					} catch (Throwable e) {
						e.printStackTrace();
					}

				}
			});
        }
        else
        {
        	ContentRetrieveReplyMessage re = new ContentRetrieveReplyMessage(false);
        	re.setDestConvId(incoming.getSrcConvId());
        	link.sendMessage(re, null);
        }
        
	}

	@Override
	public void timeout(PeerLink link, Message msg) {
		
	}

}
