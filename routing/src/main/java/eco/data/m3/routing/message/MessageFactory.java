package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import eco.data.m3.routing.KadConfiguration;
import eco.data.m3.routing.KadServer;
import eco.data.m3.routing.KademliaNode;
import eco.data.m3.routing.dht.KademliaDHT;

/**
 * Handles creating messages and receivers
 *
 * @author Joshua Kissoon
 * @since 20140202
 */
public class MessageFactory implements KademliaMessageFactory
{

    private final KademliaNode localNode;
    private final KademliaDHT dht;
    private final KadConfiguration config;

    public MessageFactory(KademliaNode local, KademliaDHT dht, KadConfiguration config)
    {
        this.localNode = local;
        this.dht = dht;
        this.config = config;
    }

    @Override
    public Message createMessage(byte code, DataInputStream in, DatagramPacket packet) throws IOException
    {
        switch (code)
        {
            case AcknowledgeMessage.CODE:
                return new AcknowledgeMessage(in, packet);
            case ConnectMessage.CODE:
                return new ConnectMessage(in, packet);
            case ContentMessage.CODE:
                return new ContentMessage(in, packet);
            case ContentLookupMessage.CODE:
                return new ContentLookupMessage(in, packet);
            case NodeLookupMessage.CODE:
                return new NodeLookupMessage(in, packet);
            case NodeReplyMessage.CODE:
                return new NodeReplyMessage(in, packet);
            case SimpleMessage.CODE:
                return new SimpleMessage(in, packet);
            case StoreContentMessage.CODE:
                return new StoreContentMessage(in, packet);
            default:
                //System.out.println(this.localNode + " - No Message handler found for message. Code: " + code);
                return new SimpleMessage(in, packet);

        }
    }

    @Override
    public Receiver createReceiver(byte code, KadServer server)
    {
        switch (code)
        {
            case ConnectMessage.CODE:
                return new ConnectReceiver(server, this.localNode);
            case ContentLookupMessage.CODE:
                return new ContentLookupReceiver(server, this.localNode, this.dht, this.config);
            case NodeLookupMessage.CODE:
                return new NodeLookupReceiver(server, this.localNode, this.config);
            case StoreContentMessage.CODE:
                return new StoreContentReceiver(server, this.localNode, this.dht);
            default:
                //System.out.println("No receiver found for message. Code: " + code);
                return new SimpleReceiver();
        }
    }
}
