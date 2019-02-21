	package eco.data.m3.routing.mnode;

    import java.util.UUID;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeContentSendingTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("ASF45678947584567467"));
		MNode node2 = host.createNode("Start2", new MId("ASERTKJDHGVHERJHGFLK"));
		
		node1.join(node2.getNodeId());

        /**
         * Lets create the content and share it
         */
        String data = "";
        for (int i = 0; i < 10; i++)
        {
            data += UUID.randomUUID();
        }
        System.out.println(data);
        MTextContent c = new MTextContent(node2.getNodeId(), new MId(), data);
        node2.putContent(c);

        /**
         * Lets retrieve the content
         */
        System.out.println("Retrieving Content");
        MContentKey gp = new MContentKey(c);

        System.out.println("Get Parameter: " + gp);
        MTextContent conte = (MTextContent) node2.get(gp);
        
        System.out.println("Content Found: " + conte);
        System.out.println("Content Metadata: " + conte.getMeta());
	}

}
