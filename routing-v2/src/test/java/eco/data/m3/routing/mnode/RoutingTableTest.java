package eco.data.m3.routing.mnode;

import java.io.IOException;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.Content;

public class RoutingTableTest {

	@Test
	public void test() throws IOException, InterruptedException {
		MHost host = new MHost();
		int numKads = 10;

        /* Setting up Kad networks */
        MNode [] kads = new MNode[10];

        kads[0] = host.createNode("user0", new MId("HRF456789SD584567460"));
        kads[1] = host.createNode("user1", new MId("ASF456789475DS567461"));
        kads[2] = host.createNode("user2", new MId("AFG45678947584567462"));
        kads[3] = host.createNode("user3", new MId("FSF45J38947584567463"));
        kads[4] = host.createNode("user4", new MId("ASF45678947584567464"));
        kads[5] = host.createNode("user5", new MId("GHF4567894DR84567465"));
        kads[6] = host.createNode("user6", new MId("ASF45678947584567466"));
        kads[7] = host.createNode("user7", new MId("AE345678947584567467"));
        kads[8] = host.createNode("user8", new MId("ASAA5678947584567468"));
        kads[9] = host.createNode("user9", new MId("ASF456789475845674U9"));

        for (int i = 1; i < numKads; i++)
        {
            kads[i].join(kads[0].getNodeId());
        }
        
        printRoutingTables(kads);
        
        kads[3].shutdown(false);

        putContent("Content owned by kad0", kads[0]);
        printStorage(kads);

        Thread.sleep(1000);

        /* kad3 should be removed from their routing tables by now. */
        printRoutingTables(kads);        
	}
	
	void printRoutingTables(MNode[] nodes) {
		for (MNode mNode : nodes) {
	        System.out.println(mNode.getRoutingTable());
		}
	}
    public void printStorage(MNode[] nodes)
    {
		for (MNode mNode : nodes) {
	        System.out.println(mNode.getDHT());
        }
    }

    public Content putContent(String content, MNode owner)
    {
        Content c = null;
        try
        {
            c = new Content(owner.getName(), "Some Data");
            owner.put(c);
            return c;
        }
        catch (IOException e)
        {
            System.err.println("Error whiles putting content " + content + " from owner: " + owner.getNodeId());
        }

        return c;
    }
}
