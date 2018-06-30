package eco.data.m3.routing.message;

import java.io.IOException;

/**
 * Default receiver if none other is called
 *
 * @author Joshua Kissoon
 * @created 20140202
 */
public class SimpleReceiver implements Receiver
{

    @Override
    public void receive(Message incoming, int conversationId)
    {
        //System.out.println("Received message: " + incoming);
    }

    @Override
    public void timeout(int conversationId) throws IOException
    {
        //System.out.println("SimpleReceiver message timeout.");
    }
}
