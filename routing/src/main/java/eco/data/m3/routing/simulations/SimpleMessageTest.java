package eco.data.m3.routing.simulations;

import java.io.IOException;
import eco.data.m3.routing.JKademliaNode;
import eco.data.m3.routing.message.SimpleMessage;
import eco.data.m3.routing.node.KademliaId;
import eco.data.m3.routing.message.SimpleReceiver;

/**
 * Test 1: Try sending a simple message between nodes
 *
 * @author Joshua Kissoon
 * @since 20140218
 */
public class SimpleMessageTest
{


    public static void main(String[] args)
    {
        try
        {
            JKademliaNode kad1 = new JKademliaNode("Joshua", new KademliaId("12345678901234567890"), 7574);
            JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("12345678901234567891"), 7572);

            kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
