package eco.data.m3.routing.mnode;

import org.junit.Test;

import io.netty.channel.nio.NioEventLoopGroup;

public class MNodeEventLoopTest {

	@Test
	public void test() throws Throwable {
		NioEventLoopGroup group = new NioEventLoopGroup();
		for(int i=0; i<50; i++) {
			final int v = i;
			group.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						System.out.println(v);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			});
		}
		Thread.sleep(10000);
	}

}
