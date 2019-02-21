package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeSimpleDHTTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		host.setPortBegin(12000);
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));		
		MNode node3 = host.createNode("Start3", new MId("12345678901234567892"));		
		MNode node4 = host.createNode("Start4", new MId("12345678901234567893"));	

		node2.join(node1.getNodeId());
		node3.join(node1.getNodeId());
		node4.join(node2.getNodeId());

		Thread.sleep(1000000);
	}

}
