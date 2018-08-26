package eco.data.m3.routing.message.handler;

import java.io.IOException;

import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.AcknowledgeMessage;
import eco.data.m3.routing.message.ConnectMessage;

/**
 * Receives a ConnectMessage and sends an AcknowledgeMessage as reply.
 * 
 * @author xquan
 *
 */
public class ConnectHandler extends MessageHandler{

    private final MNode localNode;

    public ConnectHandler(Server server)
    {
    	super(server);
        this.localNode = (MNode) server.getData();
    }
    
	@Override
	public void receive(Message incoming, int conversationId) throws IOException {
        ConnectMessage mess = (ConnectMessage) incoming;

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(mess.getOrigin());

        /* Respond to the connect request */
        AcknowledgeMessage msg = new AcknowledgeMessage(localNode.getNodeId());
        
        //System.out.println("Connect Receiver : " + msg.getOrigin().getSocketAddress());

        /* Reply to the connect message with an Acknowledgement */
        localNode.getServer().reply(mess.getOrigin(), msg, conversationId);	
	}

	@Override
	public void timeout(int conversationId) throws IOException {		
	}

}
