package eco.data.m3.net.server;


import java.io.IOException;
import java.util.TimerTask;

import eco.data.m3.net.message.MessageHandler;

/**
 * A Server Task is used for server handling jobs in a thread.
 * @author xquan
 *
 */
public class ServerTask extends TimerTask{

    private final int conversationId;
    private final MessageHandler handler;
    private final Server server;
    
    public ServerTask(Server server, int conversationId, MessageHandler handler) {
    	this.server = server;
        this.conversationId = conversationId;
        this.handler = handler;
    }
    
	@Override
	public void run() {
        if (!server.isRunning()){
            return;
        }

        try
        {
            server.unregister(conversationId);
            handler.timeout(conversationId);
        }
        catch (IOException e)
        {
            System.err.println("Cannot unregister a receiver. Message: " + e.getMessage());
        }
	}

}
