package eco.data.m3.routing.mnode;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.core.MContent;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

public class MNodeAutoRefreshTest2 {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));
		MNode node3 = host.createNode("Start3", new MId("12345678901234567892"));

        /* Connecting nodes */
        System.out.println("Connecting Nodes");
        node2.join(node1.getNodeId());
        node3.join(node2.getNodeId());

        MContent c = new MContent(node1.getName(), new MId("AS84k678947584567465"));
        c.setData("Setting the data");
        node1.putLocally(c);

        System.out.println("\n Content ID: " + c.getKey());
        System.out.println(node1.getNodeId() + " Distance from content: " + ((MId)node1.getNodeId()).getDistance((MId)c.getKey()));
        System.out.println(node2.getNodeId() + " Distance from content: " + ((MId)node2.getNodeId()).getDistance((MId)c.getKey()));
        System.out.println(node3.getNodeId() + " Distance from content: " + ((MId)node3.getNodeId()).getDistance((MId)c.getKey()));
        System.out.println("\nSTORING CONTENT 1 locally on " + node1.getName() + "\n\n\n\n");

        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);

        /* Print the node states every few minutes */
        MConfiguration config = new MConfiguration();
        Timer timer = new Timer(true);
        timer.schedule(
                new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        System.out.println(node1);
                        System.out.println(node2);
                        System.out.println(node3);
                    }
                },
                // Delay                        // Interval
                config.restoreInterval(), config.restoreInterval()
        );
	}

}
