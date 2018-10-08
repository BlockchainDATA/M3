	package eco.data.m3.routing.mnode;

    import eco.data.m3.net.core.MId;
    import eco.data.m3.routing.MHost;
    import eco.data.m3.routing.MNode;
    import eco.data.m3.routing.core.GetParameter;
    import eco.data.m3.routing.core.MContent;
    import eco.data.m3.routing.core.StorageEntry;
    import org.junit.Test;

    import java.util.UUID;

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
        for (int i = 0; i < 500; i++)
        {
            data += UUID.randomUUID();
        }
        System.out.println(data);
        MContent c = new MContent(node2.getName(), data);
        node2.putContent(c);

        /**
         * Lets retrieve the content
         */
        System.out.println("Retrieving Content");
        GetParameter gp = new GetParameter(c.getKey(), MContent.TYPE);
        gp.setOwnerId(c.getOwnerId());
        System.out.println("Get Parameter: " + gp);
        StorageEntry conte = node2.get(gp);
        System.out.println("Content Found: " + MContent.fromSerializedForm(conte.getContent()));
        System.out.println("Content Metadata: " + conte.getContentMetadata());
	}

}
