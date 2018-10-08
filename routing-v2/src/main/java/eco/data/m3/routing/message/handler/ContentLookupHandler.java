package eco.data.m3.routing.message.handler;

import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentMessage;
import eco.data.m3.routing.message.NodeLookupMessage;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Responds to a ContentLookupMessage by sending a ContentMessage containing the requested content;
 * if the requested content is not found, a NodeReplyMessage containing the K closest nodes to the request key is sent.
 * 
 * @author xquan
 *
 */
public class ContentLookupHandler extends MessageHandler{

    private final MNode localNode;

    public ContentLookupHandler(Server server)
    {
    	super(server);
        this.localNode = (MNode) server.getData();
    }

	@Override
	public void receive(Message incoming, int conversationId) throws Throwable {

        ContentLookupMessage msg = (ContentLookupMessage) incoming;
        this.localNode.getRoutingTable().insert(msg.getOrigin());

        /* Check if we can have this data */
        if (localNode.getDHT().contains(msg.getParam()))
        {
            try
            {
                /* Return a ContentMessage with the required data */
                ContentMessage cMsg = new ContentMessage(localNode.getNodeId(), localNode.getDHT().get(msg.getParam()));
                localNode.getServer().reply(msg.getOrigin(), cMsg, conversationId);
            }
            catch (NoSuchElementException ex)
            {
                /* @todo Not sure why this exception is thrown here, checkup the system when tests are writtem*/
            }
        }
        else
        {
            /**
             * Return a the K closest nodes to this content identifier
             * We create a NodeLookupReceiver and let this receiver handle this operation
             */
            NodeLookupMessage lkpMsg = new NodeLookupMessage(msg.getOrigin(), msg.getParam().getKey());
            new NodeLookupHandler(localNode.getServer()).receive(lkpMsg, conversationId);
        }		
	}

	@Override
	public void timeout(int conversationId) throws IOException {
		
	}

}
