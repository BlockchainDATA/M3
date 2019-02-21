package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeContentUpdatingTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("ASF45678947584567467"));
		MNode node2 = host.createNode("Start2", new MId("ASERTKJDHGVHERJHGFLK"));
		
		node1.join(node2.getNodeId());

        /**
         * Lets create the content and share it
         */
		MTextContent c = new MTextContent(node2.getNodeId(), new MId(), "Some Data");
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

        /* Lets update the content and put it again */
        c.setData(new String("Some New Data").getBytes());
        node2.putContent(c);

        /* Lets retrieve the content */
        System.out.println("Retrieving Content Again");
        conte = (MTextContent) node2.get(gp);
        System.out.println("Content Found: " + conte);
        System.out.println("Content Metadata: " + conte.getMeta());
	}

}
