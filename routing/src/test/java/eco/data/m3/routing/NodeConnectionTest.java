package eco.data.m3.routing;

import eco.data.m3.routing.node.KademliaId;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class NodeConnectionTest //extends TestCase
{

    //@Test
//    public void testConnect() throws IOException, InterruptedException {
//
//        JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF45678947584567467"), 7574);
//        System.out.println("Created Node Kad 1: " + kad1.getNode().getNodeId());
//
//        JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("ASERTKJDHGVHERJHGFLK"), 7572);
//
//        /* Connecting 2 to 1 */
//        System.out.println("Connecting Kad 1 and Kad 2");
//        kad1.bootstrap(kad2.getNode());
//
//        Thread.sleep(1000);
//
//        kad1.shutdown(false);
//        kad2.shutdown(false);
//    }

    //@Test
//    public void testMultiConnect() throws IOException, InterruptedException {
//
//        JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF45678947584567467"), 7574);
//        System.out.println("Created Node Kad 1: " + kad1.getNode().getNodeId());
//
//        JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("ASERTKJDHGVHERJHGFLK"), 7572);
//
//        /* Connecting 2 to 1 */
//        System.out.println("Connecting Kad 1 and Kad 2");
//        kad1.bootstrap(kad2.getNode());
//
//        /* Creating a new node 3 and connecting it to 1, hoping it'll get onto 2 also */
//        JKademliaNode kad3 = new JKademliaNode("Jessica", new KademliaId("ASERTKJDOLKMNBVFR45G"), 7783);
//        kad3.bootstrap(kad2.getNode());
//
//        JKademliaNode kad4 = new JKademliaNode("Sandy", new KademliaId("ASERTK85OLKMN85FR4SS"), 7789);
//        kad4.bootstrap(kad2.getNode());
//
//
//        Thread.sleep(1000);
//
//        kad4.shutdown(false);
//        kad3.shutdown(false);
//        kad1.shutdown(false);
//        kad2.shutdown(false);
//    }
}
