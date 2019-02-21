package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;

public class MNodeJointNodeTest3 {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		host.setPortBegin(10000);
		host.createNode("Start1", new MId("12345678901234567000"));
		
		Thread.sleep(1000000);
	}

}
