package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.impl.MBinaryContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodePutLargeTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("JoshuaK", new MId("00000000000000000000"));
		MNode node2 = host.createNode("Crystal", new MId("00000000000000000001"));

        /* Connecting nodes */
        System.out.println("Connecting Nodes");
        node2.join(node1.getNodeId());

        byte [] data = new byte[1024*1024*8]; // 8MB
        MBinaryContent c = new MBinaryContent(node1.getNodeId(), new MId(), data);

        System.out.println("\n Content ID: " + c.getMeta().getKey());
        System.out.println(node1.getNodeId() + " Distance from content: " + (node1.getNodeId()).getDistance(c.getMeta().getKey()));
        System.out.println(node2.getNodeId() + " Distance from content: " + (node2.getNodeId()).getDistance(c.getMeta().getKey()));
        
        System.out.println("\nSTORING CONTENT 1 locally on " + node1.getNodeId() + "\n\n\n\n");
        node1.putContent(c);        

        Thread.sleep(1000);
        
        System.out.println(node1);
        System.out.println(node2);
        
        System.out.println("------------------------------------------------------------------");
	}

}
