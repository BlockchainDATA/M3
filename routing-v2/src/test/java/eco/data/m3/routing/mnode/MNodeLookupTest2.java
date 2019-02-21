package eco.data.m3.routing.mnode;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeLookupTest2 {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));		
		MNode node3 = host.createNode("Start3", new MId("12345678901234567892"));		
		MNode node4 = host.createNode("Start4", new MId("12345678901234567893"));		
		MNode node5 = host.createNode("Start5", new MId("12345678901234567894"));	

		node2.join(node1.getNodeId());
		node3.join(node1.getNodeId());
		node4.join(node2.getNodeId());
		node5.join(node3.getNodeId());
		
		MTextContent c = new MTextContent(node1.getNodeId(), new MId("AS84k678947584567465"), "Set Content");
		node5.putContent(c);

		Thread.sleep(1000);

		List<MId> nodes1 = node1.lookupContent(new MContentKey(c), 3);
		List<MId> nodes2 = node2.lookupContent(new MContentKey(c), 3);
		List<MId> nodes3 = node3.lookupContent(new MContentKey(c), 3);
		List<MId> nodes4 = node4.lookupContent(new MContentKey(c), 3);
		List<MId> nodes5 = node5.lookupContent(new MContentKey(c), 3);

		assertEquals(3, nodes1.size());
		assertEquals(3, nodes2.size());
		assertEquals(3, nodes3.size());
		assertEquals(3, nodes4.size());
		assertEquals(3, nodes5.size());
	}

}
