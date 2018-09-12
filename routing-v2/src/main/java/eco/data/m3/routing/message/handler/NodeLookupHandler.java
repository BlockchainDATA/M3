package eco.data.m3.routing.message.handler;

import java.io.IOException;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;

/**
 * Receives a NodeLookupMessage and sends a NodeReplyMessage as reply with the K-Closest nodes to the ID sent.
 * 
 * @author xquan
 *
 */
public class NodeLookupHandler extends MessageHandler{

    private final MNode localNode;

    public NodeLookupHandler(Server server)
    {
    	super(server);
        this.localNode = (MNode) server.getData();
    }

	@Override
	public void receive(Message incoming, int conversationId) throws IOException {
        NodeLookupMessage msg = (NodeLookupMessage) incoming;
        MId origin = msg.getOrigin();

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(origin);

        /* Find nodes closest to the LookupId */
        List<MId> nodes = this.localNode.getRoutingTable().findClosest(msg.getLookupId(), localNode.getCurrentConfiguration().k());

        /* Respond to the NodeLookupMessage */
        Message reply = new NodeReplyMessage(this.localNode.getNodeId(), nodes);

        if (localNode.getServer().isRunning())
        {
            /* Let the Server send the reply */
        	localNode.getServer().reply(origin, reply, conversationId);
        }
		
	}

	@Override
	public void timeout(int conversationId) throws IOException {
		
	}

}
