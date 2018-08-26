package eco.data.m3.routing.mnode;

import java.io.IOException;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.algorithm.kademlia.KademliaRoutingTable;

public class MNodeSendTest {

	@Test
	public void test() throws IOException {
		MHost host = new MHost();
		
		MNode node1 = host.createNode("JoshuaK", new MId("ASF45678947584567463"));
		MNode node2 = host.createNode("Crystal", new MId("ASF45678947584567464"));
		MNode node3 = host.createNode("Shameer", new MId("ASF45678947584567465"));
		MNode node4 = host.createNode("Lokesh", new MId("ASF45678947584567466"));
		MNode node5 = host.createNode("Chandu", new MId("ASF45678947584567467"));

        KademliaRoutingTable rt = (KademliaRoutingTable) node1.getRoutingTable();
        
        rt.insert(node2.getNodeId());
        rt.insert(node3.getNodeId());
        rt.insert(node4.getNodeId());
        System.out.println(rt);
        
        rt.insert(node5.getNodeId());            
        System.out.println(rt);
        
        rt.insert(node3.getNodeId());            
        System.out.println(rt);
	}

}
