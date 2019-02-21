package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeJointTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567891"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567892"));		
		node1.join(node2.getNodeId());
		
		Thread.sleep(100000);
	}

}
