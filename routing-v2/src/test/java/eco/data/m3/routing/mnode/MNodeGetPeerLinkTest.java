package eco.data.m3.routing.mnode;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import io.netty.channel.nio.NioEventLoopGroup;

public class MNodeGetPeerLinkTest {

	@Test
	public void test() throws Throwable {

		MHost host = new MHost();
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));
		
		NioEventLoopGroup group = new NioEventLoopGroup();
		for(int i=0; i<10; i++) {
			group.execute(new Runnable() {
				@Override
				public void run() {	
					node2.getNetService().getPeerLink(new MId("12345678901234567890"));
				}
			});
		}
		
		Thread.sleep(10000);
//		node2.getNetService().printLinks();
	}

}
