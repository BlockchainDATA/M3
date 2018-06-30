package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import eco.data.m3.routing.dht.JKademliaStorageEntry;
import eco.data.m3.routing.dht.KademliaStorageEntry;
import eco.data.m3.routing.node.Node;
import eco.data.m3.routing.util.serializer.JsonSerializer;

/**
 * A StoreContentMessage used to send a store message to a node
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public class StoreContentMessage implements Message
{

    public static final byte CODE = 0x08;

    private JKademliaStorageEntry content;
    private Node origin;

    /**
     * @param origin  Where the message came from
     * @param content The content to be stored
     *
     */
    public StoreContentMessage(Node origin, JKademliaStorageEntry content)
    {
        this.content = content;
        this.origin = origin;
    }

    public StoreContentMessage(DataInputStream in, DatagramPacket packet) throws IOException
    {
        this.fromStream(in, packet);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        this.origin.toStream(out);

        /* Serialize the KadContent, then send it to the stream */
        new JsonSerializer<JKademliaStorageEntry>().write(content, out);
    }

    @Override
    public final void fromStream(DataInputStream in, DatagramPacket packet) throws IOException
    {
        this.origin = new Node(in, packet);
        try
        {
            this.content = new JsonSerializer<JKademliaStorageEntry>().read(in);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    public JKademliaStorageEntry getContent()
    {
        return this.content;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "StoreContentMessage[origin=" + origin + ",content=" + content + "]";
    }
}
