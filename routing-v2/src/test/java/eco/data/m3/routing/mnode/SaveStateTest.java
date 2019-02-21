package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class SaveStateTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("JoshuaK", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Crystal", new MId("12345678901234567891"));

        /* Connecting 2 to 1 */
        System.out.println("Connecting Nodes 1 & 2");
        node2.join(node1.getNodeId());
        System.out.println(node1);
        System.out.println(node2);

        MTextContent c;
        System.out.println("\n\n\n\nSTORING CONTENT 1\n\n\n\n");
        c = new MTextContent(node2.getNodeId(), new MId(), "Some Data");
        System.out.println(c);
        node1.putContent(c);

        System.out.println(node1);
        System.out.println(node2);

        /* Shutting down node1 and restarting it */
        System.out.println("\n\n\nShutting down Kad 1 instance");
        node1.shutdown();

        System.out.println("\n\n\nReloading Kad instance from file");
        node1 = host.createNode(node1.getName(), node1.getNodeId());
        node1.join(node2.getNodeId());
        System.out.println(node2);

        /* Trying to get a content stored on the restored node */
        MContentKey gp = new MContentKey(c);
        MTextContent content = (MTextContent) node2.get(gp);
        System.out.println("Content received: " + content);
	}

}
