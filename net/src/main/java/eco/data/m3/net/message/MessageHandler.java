package eco.data.m3.net.message;

import java.io.IOException;

import eco.data.m3.net.server.Server;

/**
 * Message Handler's Base class.
 * All Handlers should implement this functions.
 * 
 * @author xquan
 *
 */
public abstract class MessageHandler {
	
	private Server server;

	public abstract void receive(Message incoming, int conversationId) throws IOException;
	
    public abstract void timeout(int conversationId) throws IOException;

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}
    
}
