package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import eco.data.m3.routing.KadServer;

/**
 * A factory that handles creating messages and receivers
 *
 * @author Joshua Kissoon
 * @since 20140523
 */
public interface KademliaMessageFactory
{

    /**
     * Method that creates a message based on the code and input stream
     *
     * @param code The message code
     * @param in   An input stream with the message data
     *
     * @return A message
     *
     * @throws java.io.IOException
     */
    public Message createMessage(byte code, DataInputStream in, DatagramPacket packet) throws IOException;

    /**
     * Method that returns a receiver to handle a specific type of message
     *
     * @param code   The message code
     * @param server
     *
     * @return A receiver
     */
    public Receiver createReceiver(byte code, KadServer server);
}
