package eco.data.m3.routing.message;

import java.io.IOException;
import eco.data.m3.routing.KadServer;
import eco.data.m3.routing.KademliaNode;

/**
 * Receives a ConnectMessage and sends an AcknowledgeMessage as reply.
 *
 * @author Joshua Kissoon
 * @since 20140219
 */
public class ConnectReceiver implements Receiver
{

    private final KadServer server;
    private final KademliaNode localNode;

    public ConnectReceiver(KadServer server, KademliaNode local)
    {
        this.server = server;
        this.localNode = local;
    }

    /**
     * Handle receiving a ConnectMessage
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void receive(Message incoming, int comm) throws IOException
    {
        ConnectMessage mess = (ConnectMessage) incoming;

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(mess.getOrigin());

        /* Respond to the connect request */
        AcknowledgeMessage msg = new AcknowledgeMessage(this.localNode.getNode());
        
        //System.out.println("Connect Receiver : " + msg.getOrigin().getSocketAddress());

        /* Reply to the connect message with an Acknowledgement */
        this.server.reply(mess.getOrigin(), msg, comm);
    }

    /**
     * We don't need to do anything here
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void timeout(int comm) throws IOException
    {
    }
}
