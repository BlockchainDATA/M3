package eco.data.m3.routing.mnode;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.Content;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.core.DHTType;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.exception.ContentNotFoundException;

public class MNodeRefreshTest {

	@Test
	public void test() throws IOException, NoSuchElementException, ContentNotFoundException, InterruptedException {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));
		node2.join(node1.getNodeId());

        /* Lets create the content and share it */
        Content c = new Content(node2.getName(), "Some Data");
        node2.put(c);

        /* Lets retrieve the content */
        GetParameter gp = new GetParameter(c.getKey(), Content.TYPE);
        gp.setType(Content.TYPE);
        gp.setOwnerId(c.getOwnerId());
        StorageEntry conte = node2.get(gp);

        node2.refresh();
        
        Thread.sleep(10000);
	}

}
