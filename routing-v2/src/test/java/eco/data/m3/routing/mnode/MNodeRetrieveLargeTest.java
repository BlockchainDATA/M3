package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MBinaryContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeRetrieveLargeTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));		
		node1.join(node2.getNodeId());

        byte [] data = new byte[1024*1024*8]; // 8MB
		MBinaryContent c = new MBinaryContent(node1.getNodeId(), new MId(), data);
//		MContent c = new MContent(node1.getNodeId(), new MId("AS84k678947584567465"), "Setting the data");
		node1.putContentLocally(c);
		
		node2.retrive(node1.getNodeId(), new MContentKey(c));
		
		Thread.sleep(1000);
		
		System.out.println(node1);
		System.out.println(node2);

	}

}
