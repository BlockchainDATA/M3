package eco.data.m3.routing.message.handler;

import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.StoreContentReplyMessage;

import java.io.IOException;

/**
 * Receiver for incoming StoreContentMessage
 * 
 * @author xquan
 *
 */
public class StoreContentHandler extends MessageHandler{

    private final MNode localNode;

    private static int counter = 0;

    public StoreContentHandler(Server server)
    {
    	super(server);
        this.localNode = (MNode) server.getData();
    }


	@Override
	public void receive(Message incoming, int conversationId) throws Throwable {
        /* It's a StoreContentMessage we're receiving */
        StoreContentMessage msg = (StoreContentMessage) incoming;

        /* Insert the message sender into this node's routing table */
        this.localNode.getRoutingTable().insert(msg.getOrigin());

        StoreContentReplyMessage reMsg = new StoreContentReplyMessage(server.getMId());
        try
        {
            /* Store this Content into the DHT */
        	if(localNode.getDHT().store(msg.getContent())) {
            	System.out.println("Store (" + StoreContentHandler.counter++ +"): " + msg.getContent().getContentMetadata().getKey());
        		reMsg.setCode(0);
        	}else
        		reMsg.setCode(1);
        }
        catch (IOException e)
        {
            System.err.println("Unable to store received content; Message: " + e.getMessage());
            reMsg.setCode(2);
        }
        
        localNode.getServer().reply(msg.getOrigin(), reMsg, conversationId);
	}

	@Override
	public void timeout(int conversationId) throws IOException {
		
	}

}
