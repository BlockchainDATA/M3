package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodePutTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("JoshuaK", new MId("00000000000000000000"));
		MNode node2 = host.createNode("Crystal", new MId("00000000000000000001"));
		MNode node3 = host.createNode("Shameer", new MId("00000000000000000002"));
		MNode node4 = host.createNode("Lokesh", new MId("00000000000000000003"));
		MNode node5 = host.createNode("Chandu", new MId("00000000000000000004"));

        Thread.sleep(1000);
        /* Connecting nodes */
        System.out.println("Connecting Nodes");
        node2.join(node1.getNodeId());
        node3.join(node2.getNodeId());
        node4.join(node2.getNodeId());
        node5.join(node4.getNodeId());

        MTextContent c = new MTextContent(node1.getNodeId(), new MId("AS84k678947584567465"), "Setting the data");

        System.out.println("\n Content ID: " + c.getMeta().getKey());
        System.out.println(node1.getNodeId() + " Distance from content: " + (node1.getNodeId()).getDistance(c.getMeta().getKey()));
        System.out.println(node2.getNodeId() + " Distance from content: " + (node2.getNodeId()).getDistance(c.getMeta().getKey()));
        System.out.println(node3.getNodeId() + " Distance from content: " + (node3.getNodeId()).getDistance(c.getMeta().getKey()));
        System.out.println(node4.getNodeId() + " Distance from content: " + (node1.getNodeId()).getDistance(c.getMeta().getKey()));
        System.out.println(node5.getNodeId() + " Distance from content: " + (node2.getNodeId()).getDistance(c.getMeta().getKey()));
        
        System.out.println("\nSTORING CONTENT 1 locally on " + node1.getNodeId() + "\n\n\n\n");
        node1.putContent(c);

        Thread.sleep(100000);
        System.out.println("------------------------------------------------------------------");
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);
        System.out.println(node4);
        System.out.println(node5);


	}

}
