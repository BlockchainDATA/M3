package eco.data.m3.routing.message.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.BatchedNodeLookupMessage;
import eco.data.m3.routing.message.BatchedNodeReplyMessage;

/**
 * Receives a NodeLookupMessage and sends a NodeReplyMessage as reply with the K-Closest nodes to the ID sent.
 * 
 * @author xquan
 *
 */
public class BatchedNodeLookupHandler extends MessageHandler{

    private final MNode localNode;

    public BatchedNodeLookupHandler(Server server)
    {
    	super(server);
        this.localNode = (MNode) server.getData();
    }

	@Override
	public void receive(Message incoming, int conversationId) throws Throwable {
		BatchedNodeLookupMessage msg = (BatchedNodeLookupMessage) incoming;
        MId origin = msg.getOrigin();

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(origin);

        List<List<MId>> nodeArrays = new ArrayList<>();
        for (MId lookupId : msg.getLookupIds()) {
            /* Find nodes closest to the LookupId */
            List<MId> nodes = this.localNode.getRoutingTable().findClosest(lookupId, localNode.getCurrentConfiguration().k());
            nodeArrays.add(nodes);
		}

        /* Respond to the NodeLookupMessage */
        Message reply = new BatchedNodeReplyMessage(this.localNode.getNodeId(), nodeArrays);

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
