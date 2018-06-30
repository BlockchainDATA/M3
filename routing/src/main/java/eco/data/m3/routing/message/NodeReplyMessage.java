package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import eco.data.m3.routing.node.Node;

/**
 * A message used to connect nodes.
 * When a NodeLookup Request comes in, we respond with a NodeReplyMessage.
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class NodeReplyMessage implements Message
{

    private Node origin;
    public static final byte CODE = 0x06;
    private List<Node> nodes;

    public NodeReplyMessage(Node origin, List<Node> nodes)
    {
        this.origin = origin;
        this.nodes = nodes;
    }

    public NodeReplyMessage(DataInputStream in, DatagramPacket packet) throws IOException
    {
        this.fromStream(in, packet);
    }

    @Override
    public final void fromStream(DataInputStream in, DatagramPacket packet) throws IOException
    {
        /* Read in the origin */
        this.origin = new Node(in, packet);

        /* Get the number of incoming nodes */
        int len = in.readInt();
        this.nodes = new ArrayList<>(len);

        /* Read in all nodes */
        for (int i = 0; i < len; i++)
        {
            this.nodes.add(new Node(in, packet));
        }
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the origin node to the stream */
        origin.toStream(out);

        /* Add all other nodes to the stream */
        int len = this.nodes.size();
        if (len > 255)
        {
            throw new IndexOutOfBoundsException("Too many nodes in list to send in NodeReplyMessage. Size: " + len);
        }

        /* Writing the nodes to the stream */
        out.writeInt(len);
        for (Node n : this.nodes)
        {
            n.toStream(out);
        }
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

    public List<Node> getNodes()
    {
        return this.nodes;
    }

    @Override
    public String toString()
    {
        return "NodeReplyMessage[origin NodeId=" + origin.getNodeId() + "]";
    }
}
