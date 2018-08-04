package eco.data.m3.net.server;

import eco.data.m3.net.message.MessageFactory;

/**
 * Configuration to create a new server. 
 * 
 * @author xquan
 *
 */
public class ServerConfig {
	
	public static final int PORT_MIN = 10000;
	
	public static final int PORT_MAX = 20000;
	
	private ServerNetType netType = ServerNetType.UDP;

	private long responseTimeout = 10 * 1000; //10s
	
	private int port = -1;
	
	private boolean isTesting = false;
	
	private MessageFactory messageFactory = null;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getResponseTimeout() {
		return responseTimeout;
	}

	public void setResponseTimeout(long responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

	public boolean isTesting() {
		return isTesting;
	}

	public void setTesting(boolean isTesting) {
		this.isTesting = isTesting;
	}

	public ServerNetType getNetType() {
		return netType;
	}

	public void setNetType(ServerNetType netType) {
		this.netType = netType;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
}
