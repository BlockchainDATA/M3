package eco.data.m3.routing.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import eco.data.m3.routing.message.Streamable;

/**
 * A Node in the Kademlia network - Contains basic node network information.
 *
 * @author Joshua Kissoon
 * @since 20140202
 * @version 0.1
 */
public class Node implements Streamable, Serializable
{

    private KademliaId nodeId;
    private InetAddress inetAddress;
    private int port;
    private final String strRep;

    public Node(KademliaId nid, InetAddress ip, int port)
    {
        this.nodeId = nid;
        this.inetAddress = ip;
        this.port = port;
        this.strRep = this.nodeId.toString();
    }

    /**
     * Load the Node's data from a DataInput stream
     *
     * @param in
     *
     * @throws IOException
     */
    public Node(DataInputStream in, DatagramPacket packet) throws IOException
    {
        this.fromStream(in, packet);
        this.strRep = this.nodeId.toString();
    }

    /**
     * Set the InetAddress of this node
     *
     * @param addr The new InetAddress of this node
     */
    public void setInetAddress(InetAddress addr)
    {
        this.inetAddress = addr;
    }

    /**
     * @return The NodeId object of this node
     */
    public KademliaId getNodeId()
    {
        return this.nodeId;
    }

    /**
     * Creates a SocketAddress for this node
     *
     * @return
     */
    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(this.inetAddress, this.port);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the NodeId to the stream */
        this.nodeId.toStream(out);

        /* Add the Node's IP address to the stream */
        byte[] a = inetAddress.getAddress();
        if (a.length != 4)
        {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got " + a.length);
        }
        out.write(a);

        /* Add the port to the stream */
        out.writeInt(port);
    }

    @Override
    public final void fromStream(DataInputStream in, DatagramPacket packet) throws IOException
    {
        /* Load the NodeId */
        this.nodeId = new KademliaId(in, packet);

        /* Load the IP Address */
        byte[] ip = new byte[4];
        in.readFully(ip);
        this.inetAddress = InetAddress.getByAddress(ip);

        /* Read in the port */
        this.port = in.readInt();
        
        this.inetAddress = packet.getAddress();
        this.port = packet.getPort();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Node)
        {
            Node n = (Node) o;
            if (n == this)
            {
                return true;
            }
            return this.getNodeId().equals(n.getNodeId());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getNodeId().hashCode();
    }

    @Override
    public String toString()
    {
        return this.getNodeId().toString();
    }
}
