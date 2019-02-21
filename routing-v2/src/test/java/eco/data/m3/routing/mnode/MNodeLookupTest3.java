package eco.data.m3.routing.mnode;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import eco.data.m3.content.MContentKey;
import eco.data.m3.content.impl.MTextContent;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;

public class MNodeLookupTest3 {

	@Test
	public void test() throws Throwable {
		MHost host = new MHost();	
		MNode node5 = host.createNode("Start5", new MId("12345678901234567894"));	

		node5.join(new MId("12345678901234567892"));
		
		MTextContent c = new MTextContent(node5.getNodeId(), new MId("AS84k678947584567465"), "Set Content");
		node5.putContent(c);

		Thread.sleep(1000);
		List<MId> nodes4 = node5.lookupContent(new MContentKey(c), 3);

		assertEquals(3, nodes4.size());
	}

}
