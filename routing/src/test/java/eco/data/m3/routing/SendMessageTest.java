package eco.data.m3.routing;

import eco.data.m3.routing.message.SimpleMessage;
import eco.data.m3.routing.message.SimpleReceiver;
import eco.data.m3.routing.node.KademliaId;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class SendMessageTest extends TestCase {

    @Test
    public void testSendMessage() throws IOException {

        JKademliaNode kad1 = new JKademliaNode("Joshua", new KademliaId("12345678901234567890"), 7574);
        JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("12345678901234567891"), 7572);

        kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
    }
}
