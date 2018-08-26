package eco.data.m3.routing.mnode;

import java.io.IOException;

import org.junit.Test;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MHost;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.core.DHTType;

public class MNodeConnectionTest {

	@Test
	public void test() throws IOException {
		MHost host = new MHost();
		MNode node1 = host.createNode("Start1", new MId("12345678901234567890"));
		MNode node2 = host.createNode("Start2", new MId("12345678901234567891"));

        System.out.println("Created Node Kad 1: " + node1.getNodeId());
        System.out.println("Created Node Kad 2: " + node2.getNodeId());

        System.out.println("Connecting Kad 1 and Kad 2");
        node1.join(node2.getNodeId());

//        System.out.println("Kad 1: ");
//        System.out.println(kad1.getNode().getRoutingTable());
//        System.out.println("Kad 2: ");
//        System.out.println(kad2.getNode().getRoutingTable());

        /* Creating a new node 3 and connecting it to 1, hoping it'll get onto 2 also */
        MNode node3 = host.createNode("Jessica", new MId("ASERTKJDOLKMNBVFR45G"));
        System.out.println("\n\n\n\n\n\nCreated Node Kad 3: " + node3.getNodeId());

        System.out.println("Connecting Kad 3 and Kad 2");
        node3.join(node2.getNodeId());

        MNode node4 = host.createNode("Sandy", new MId("ASERTK85OLKMN85FR4SS"));
        System.out.println("\n\n\n\n\n\nCreated Node Kad 4: " + node4.getNodeId());

        System.out.println("Connecting Kad 4 and Kad 2");
        node4.join(node2.getNodeId());

        System.out.println("\n\nKad 1: " + node1.getNodeId() + " Routing Table: ");
        System.out.println(node1.getRoutingTable());
        System.out.println("\n\nKad 2: " + node2.getNodeId() + " Routing Table: ");
        System.out.println(node2.getRoutingTable());
        System.out.println("\n\nKad 3: " + node3.getNodeId() + " Routing Table: ");
        System.out.println(node3.getRoutingTable());
        System.out.println("\n\nKad 4: " + node4.getNodeId() + " Routing Table: ");
        System.out.println(node4.getRoutingTable());
	}

}
