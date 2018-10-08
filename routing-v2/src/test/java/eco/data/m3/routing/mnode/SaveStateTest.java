package eco.data.m3.routing.mnode;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.core.MContent;
import eco.data.m3.routing.core.StorageEntry;
import org.junit.Test;

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

        MContent c;
        synchronized (this)
        {
            System.out.println("\n\n\n\nSTORING CONTENT 1\n\n\n\n");
            c = new MContent(node2.getName(), "Some Data");
            System.out.println(c);
            node1.putLocally(c);
        }

        System.out.println(node1);
        System.out.println(node2);

        /* Shutting down node1 and restarting it */
        System.out.println("\n\n\nShutting down Kad 1 instance");
        node1.shutdown(true);

        System.out.println("\n\n\nReloading Kad instance from file");
        node1 = host.loadFromFile("JoshuaK");
        node1.join(node2.getNodeId());
        System.out.println(node2);

        /* Trying to get a content stored on the restored node */
        GetParameter gp = new GetParameter(c.getKey(), node2.getName(), c.getType());
        StorageEntry content = node2.get(gp);
        MContent cc = MContent.fromSerializedForm(content.getContent());
        System.out.println("Content received: " + cc);
	}

}
