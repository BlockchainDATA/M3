package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.MContent;
import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class SaveStateTest2 {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("JoshuaK", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Crystal", new MId("12345678901234567891"));
		MNode node3 = host.createNode("Shameer", new MId("12345678901234567892"));
		MNode node4 = host.createNode("Lokesh", new MId("12345678901234567893"));
		MNode node5 = host.createNode("Chandu", new MId("12345678901234567894"));


        /* Connecting 2 to 1 */
        System.out.println("Connecting Nodes 1 & 2");
        node2.join(node1.getNodeId());
        System.out.println(node1);
        System.out.println(node2);

        node3.join(node2.getNodeId());
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);

        node4.join(node2.getNodeId());
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);
        System.out.println(node4);

        node5.join(node4.getNodeId());

        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);
        System.out.println(node4);
        System.out.println(node5);

        synchronized (this)
        {
            System.out.println("\n\n\n\nSTORING CONTENT 1\n\n\n\n");
            MTextContent c = new MTextContent(node2.getNodeId(), new MId(), "Some Data");
            System.out.println(c);
            node2.putContent(c);
        }

        MTextContent c2;
        synchronized (this)
        {
            System.out.println("\n\n\n\nSTORING CONTENT 2\n\n\n\n");
            c2 = new MTextContent(node2.getNodeId(), new MId(), "Some other Data");
            System.out.println(c2);
            node4.putContent(c2);
        }

        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);
        System.out.println(node4);
        System.out.println(node5);

        /* Shutting down node1 and restarting it */
        System.out.println("\n\n\nShutting down Kad instance");
        System.out.println(node2);
        node1.shutdown();

        System.out.println("\n\n\nReloading Kad instance from file");
        MNode nodeR2 = host.createNode(node1.getName(), node1.getNodeId()); 
        System.out.println(nodeR2);

        /* Trying to get a content stored on the restored node */
        MContentKey gp = new MContentKey(c2);
        MContent content = nodeR2.get(gp);
        System.out.println("Content received: " + content);
	}

}
