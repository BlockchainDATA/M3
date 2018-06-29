package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import eco.data.m3.routing.node.Node;

/**
 * A message sent to another node requesting to connect to them.
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class ConnectMessage implements Message
{

    private Node origin;
    public static final byte CODE = 0x02;

    public ConnectMessage(Node origin)
    {
        this.origin = origin;
    }

    public ConnectMessage(DataInputStream in, DatagramPacket packet) throws IOException
    {
        this.fromStream(in, packet);
    }

    @Override
    public final void fromStream(DataInputStream in, DatagramPacket packet) throws IOException
    {
        this.origin = new Node(in, packet);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        origin.toStream(out);
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "ConnectMessage[origin NodeId=" + origin.getNodeId() + "]";
    }
}
