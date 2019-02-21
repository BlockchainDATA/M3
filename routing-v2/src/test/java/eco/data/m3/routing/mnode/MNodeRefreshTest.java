package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeRefreshTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));
		node2.join(node1.getNodeId());

        /* Lets create the content and share it */
		MTextContent c = new MTextContent(node2.getNodeId(), new MId(), "Some Data");
        node2.putContent(c);

        /* Lets retrieve the content */
        MContentKey gp = new MContentKey(c);
        node2.get(gp);

        node2.refresh();
        
        Thread.sleep(1000);
	}

}
