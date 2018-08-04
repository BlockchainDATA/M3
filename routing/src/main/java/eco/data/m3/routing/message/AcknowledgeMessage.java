package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import eco.data.m3.routing.node.Node;

/**
 * A message used to acknowledge a request from a node; can be used in many situations.
 * - Mainly used to acknowledge a connect message
 *
 * @author Joshua Kissoon
 * @since 20140218
 */
public class AcknowledgeMessage implements Message
{

    private Node origin;
    public static final byte CODE = 0x01;

    public AcknowledgeMessage(Node origin)
    {
        this.origin = origin;
    }

    public AcknowledgeMessage(DataInputStream in, DatagramPacket packet) throws IOException
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
        return "AcknowledgeMessage[origin=" + origin.getNodeId() + "]";
    }
}
