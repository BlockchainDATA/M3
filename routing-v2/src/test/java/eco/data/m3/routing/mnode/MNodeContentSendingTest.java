	package eco.data.m3.routing.mnode;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.Content;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.core.DHTType;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.exception.ContentNotFoundException;

public class MNodeContentSendingTest {

	@Test
	public void test() throws IOException, NoSuchElementException, ContentNotFoundException {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("ASF45678947584567467"));
		MNode node2 = host.createNode("Start2", new MId("ASERTKJDHGVHERJHGFLK"));
		
		node1.join(node2.getNodeId());

        /**
         * Lets create the content and share it
         */
        String data = "";
        for (int i = 0; i < 500; i++)
        {
            data += UUID.randomUUID();
        }
        System.out.println(data);
        Content c = new Content(node2.getName(), data);
        node2.put(c);

        /**
         * Lets retrieve the content
         */
        System.out.println("Retrieving Content");
        GetParameter gp = new GetParameter(c.getKey(), Content.TYPE);
        gp.setOwnerId(c.getOwnerId());
        System.out.println("Get Parameter: " + gp);
        StorageEntry conte = node2.get(gp);
        System.out.println("Content Found: " + Content.fromSerializedForm(conte.getContent()));
        System.out.println("Content Metadata: " + conte.getContentMetadata());
	}

}
