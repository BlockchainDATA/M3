package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeJointNodeTest2 {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();

		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));		
		node2.join( new MId("12345678901234567890"));
		
		Thread.sleep(100000);
	}

}
