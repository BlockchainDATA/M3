/**
 * Implementation of the Kademlia Ping operation,
 * This is on hold at the moment since I'm not sure if we'll use ping given the improvements mentioned in the paper.
 *
 * @author Joshua Kissoon
 * @since 20140218
 */
package eco.data.m3.routing.operation;

import java.io.IOException;
import eco.data.m3.routing.KadServer;
import eco.data.m3.routing.exceptions.RoutingException;
import eco.data.m3.routing.node.Node;

public class PingOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;
    private final Node toPing;

    /**
     * @param server The Kademlia server used to send & receive messages
     * @param local  The local node
     * @param toPing The node to send the ping message to
     */
    public PingOperation(KadServer server, Node local, Node toPing)
    {
        this.server = server;
        this.localNode = local;
        this.toPing = toPing;
    }

    @Override
    public void execute() throws IOException, RoutingException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
