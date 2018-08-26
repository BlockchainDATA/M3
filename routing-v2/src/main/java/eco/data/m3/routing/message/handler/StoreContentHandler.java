package eco.data.m3.routing.message.handler;

import java.io.IOException;

import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.DHT;
import eco.data.m3.routing.message.StoreContentMessage;

/**
 * Receiver for incoming StoreContentMessage
 * 
 * @author xquan
 *
 */
public class StoreContentHandler extends MessageHandler{

    private final MNode localNode;

    public StoreContentHandler(Server server)
    {
    	super(server);
        this.localNode = (MNode) server.getData();
    }


	@Override
	public void receive(Message incoming, int conversationId) throws IOException {
        /* It's a StoreContentMessage we're receiving */
        StoreContentMessage msg = (StoreContentMessage) incoming;

        /* Insert the message sender into this node's routing table */
        this.localNode.getRoutingTable().insert(msg.getOrigin());

        try
        {
            /* Store this Content into the DHT */
        	localNode.getDHT().store(msg.getContent());
        }
        catch (IOException e)
        {
            System.err.println("Unable to store received content; Message: " + e.getMessage());
        }
		
	}

	@Override
	public void timeout(int conversationId) throws IOException {
		
	}

}
