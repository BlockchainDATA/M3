package eco.data.m3.net.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.exception.ServerDownException;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageFactory;
import eco.data.m3.net.message.MessageHandler;

/**
 * An Abstract Net Server.
 * Define main functions all Server Class should have.
 * 
 * @author xquan
 *
 */
public abstract class Server {
	
    protected boolean isRunning = true;
    
    private ServerNetType netType = null;
    
    private ServerConfig config;

    private final Timer timer = new Timer(true);      // Schedule future tasks

    private final Map<Integer, MessageHandler> handlers = new HashMap<>();

	private final Map<Integer, TimerTask> tasks = new HashMap<>();    // Keep track of scheduled tasks

	private MessageFactory messageFactory ;
	
	private MId mId;

	public Server(ServerConfig config, MId mid) {
		this.config = config;
		this.mId = mid;
		this.messageFactory = config.getMessageFactory();
	}
		
	public void listen() {
        new Thread()
        {
            @Override
            public void run()
            {
                doListen();
            }
        }.start();
	}
	
	public synchronized int sendMessage(MId to, Message msg, MessageHandler handler) throws IOException{
        if (!isRunning)
        {
            throw new ServerDownException("Server is not running.");
        }

        /* Generate a random communication ID */
        int comm = new Random().nextInt();

        /* If we have a receiver */
        if (handler != null)
        {
            try
            {
                /* Setup the receiver to handle message response */
                handlers.put(comm, handler);
                TimerTask task = new ServerTask(this, comm, handler);
                timer.schedule(task, this.config.getResponseTimeout());
                tasks.put(comm, task);
            }
            catch (IllegalStateException ex)
            {
                /* The timer is already cancelled so we cannot do anything here really */
            }
        }

        /* Send the message */
        sendMessage(to, msg, comm);

        return comm;	
	}
	
	public synchronized void reply(MId to, Message msg, int comm) throws IOException
	{
        if (!isRunning)
        {
            throw new ServerDownException("Server is not running.");
        }

        sendMessage(to, msg, comm);
	}
	
	public synchronized void unregister(int conversationId) {
        handlers.remove(conversationId);
        this.tasks.remove(conversationId);
	}
	
	public void shutdown() {
        this.isRunning = false;
        timer.cancel();
	}

	public abstract int getPort() ;
	
	protected abstract void doListen();
	
	protected abstract void sendMessage(MId to, Message msg, int comm) throws IOException;

	public boolean isRunning() {
		return isRunning;
	}

	public ServerNetType getNetType() {
		return netType;
	}
	
	public ServerConfig getServerConfig() {
		return config;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

    public Map<Integer, MessageHandler> getHandlers() {
		return handlers;
	}
    
    public Map<Integer, TimerTask> getTasks() {
		return tasks;
	}

	public MId getMId() {
		return mId;
	}

}
