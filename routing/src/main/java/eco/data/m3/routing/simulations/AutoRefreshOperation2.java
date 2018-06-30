package eco.data.m3.routing.simulations;

import java.util.Timer;
import java.util.TimerTask;
import eco.data.m3.routing.DefaultConfiguration;
import eco.data.m3.routing.JKademliaNode;
import eco.data.m3.routing.KadConfiguration;
import eco.data.m3.routing.node.KademliaId;

/**
 * Testing the Kademlia Auto Content and Node table refresh operations
 *
 * @author Joshua Kissoon
 * @since 20140309
 */
public class AutoRefreshOperation2 implements Simulation
{

    @Override
    public void runSimulation()
    {
        try
        {
            /* Setting up 2 Kad networks */
            final JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF456789djem4567463"), 12049);
            final JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("AS84k678DJRW84567465"), 4585);
            final JKademliaNode kad3 = new JKademliaNode("Shameer", new KademliaId("AS84k67894758456746A"), 8104);

            /* Connecting nodes */
            System.out.println("Connecting Nodes");
            kad2.bootstrap(kad1.getNode());
            kad3.bootstrap(kad2.getNode());

            DHTContentImpl c = new DHTContentImpl(new KademliaId("AS84k678947584567465"), kad1.getOwnerId());
            c.setData("Setting the data");
            kad1.putLocally(c);

            System.out.println("\n Content ID: " + c.getKey());
            System.out.println(kad1.getNode() + " Distance from content: " + kad1.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println(kad2.getNode() + " Distance from content: " + kad2.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println(kad3.getNode() + " Distance from content: " + kad3.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println("\nSTORING CONTENT 1 locally on " + kad1.getOwnerId() + "\n\n\n\n");

            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);

            /* Print the node states every few minutes */
            KadConfiguration config = new DefaultConfiguration();
            Timer timer = new Timer(true);
            timer.schedule(
                    new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            System.out.println(kad1);
                            System.out.println(kad2);
                            System.out.println(kad3);
                        }
                    },
                    // Delay                        // Interval
                    config.restoreInterval(), config.restoreInterval()
            );
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
