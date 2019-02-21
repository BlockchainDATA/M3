package eco.data.m3.routing.mnode;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeLookupTest {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));		
		node1.join(node2.getNodeId());
		
		MTextContent c = new MTextContent(node1.getNodeId(), new MId("AS84k678947584567465"), "Set Z");
		node1.putContent(c);

		Thread.sleep(1000);

		List<MId> nodes1 = node1.lookupContent(new MContentKey(c), 2);
		List<MId> nodes2 = node2.lookupContent(new MContentKey(c), 2);

		assertEquals(1, nodes1.size());
		assertEquals(1, nodes2.size());
	}

}
