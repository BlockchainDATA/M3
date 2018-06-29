package eco.data.m3.routing.simulations;

import java.io.IOException;

import eco.data.m3.routing.JKademliaNode;
import eco.data.m3.routing.node.KademliaId;

public class SimReceiverTest {

    public static void main(String[] args)
    {
        try
        {
        	System.out.println("Init done");
            JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("12345678901234567891"), 7572);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
