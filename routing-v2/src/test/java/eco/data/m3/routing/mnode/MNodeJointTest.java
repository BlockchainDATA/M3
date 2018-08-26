package eco.data.m3.routing.mnode;

import java.io.IOException;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.DHTType;

public class MNodeJointTest {

	@Test
	public void test() throws IOException {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));
		
		node1.join(node2.getNodeId());
	}

}
