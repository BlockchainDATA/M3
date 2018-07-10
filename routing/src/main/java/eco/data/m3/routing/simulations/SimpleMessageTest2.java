package eco.data.m3.routing.simulations;

import eco.data.m3.routing.JKademliaNode;
import eco.data.m3.routing.message.Message;
import eco.data.m3.routing.message.Receiver;
import eco.data.m3.routing.message.SimpleMessage;
import eco.data.m3.routing.message.SimpleReceiver;
import eco.data.m3.routing.node.KademliaId;

import java.io.IOException;

/**
 * Test 1: Try sending a simple message between nodes
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class SimpleMessageTest2
{


    public static void main(String[] args)
    {
        try
        {
            JKademliaNode kad1 = new JKademliaNode("Joshua", new KademliaId("12345678901234567890"), 7574);
            JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("12345678901234567891"), 7572);

            kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new Receiver() {
                @Override
                public void receive(Message incoming, int conversationId) throws IOException {
                    System.out.print("receive");
                }

                @Override
                public void timeout(int conversationId) throws IOException {

                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
