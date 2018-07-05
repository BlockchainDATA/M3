package eco.data.m3.routing;

import eco.data.m3.routing.node.KademliaId;
import eco.data.m3.routing.simulations.DHTContentImpl;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class SaveStateTest extends TestCase {

    @Test
    public void testSaveState() throws IOException, ClassNotFoundException {

        /* Setting up 2 Kad networks */
        JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF45678947584567463"), 12049);
        JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("ASF45678947584567464"), 4585);
        JKademliaNode kad3 = new JKademliaNode("Shameer", new KademliaId("ASF45678947584567465"), 8104);
        JKademliaNode kad4 = new JKademliaNode("Lokesh", new KademliaId("ASF45678947584567466"), 8335);
        JKademliaNode kad5 = new JKademliaNode("Chandu", new KademliaId("ASF45678947584567467"), 13345);

        /* Connecting */
        kad2.bootstrap(kad1.getNode());
        kad3.bootstrap(kad2.getNode());
        kad4.bootstrap(kad2.getNode());
        kad5.bootstrap(kad4.getNode());

        synchronized (this)
        {
            DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
            kad2.put(c);
        }

        synchronized (this)
        {
            DHTContentImpl c2 = new DHTContentImpl(kad2.getOwnerId(), "Some other Data");
            kad4.put(c2);
        }

        kad1.shutdown(true);

        JKademliaNode kadR2 = JKademliaNode.loadFromFile("JoshuaK");

    }
}
