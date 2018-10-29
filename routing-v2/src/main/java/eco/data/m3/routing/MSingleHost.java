package eco.data.m3.routing;

import java.io.IOException;
import java.net.SocketException;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.MessageFactory;
import eco.data.m3.net.server.ServerConfig;
import eco.data.m3.net.server.ServerNetType;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.message.AcknowledgeMessage;
import eco.data.m3.routing.message.ConnectMessage;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentMessage;
import eco.data.m3.routing.message.MessageCode;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.StoreContentReplyMessage;
import eco.data.m3.routing.message.handler.ConnectHandler;
import eco.data.m3.routing.message.handler.ContentLookupHandler;
import eco.data.m3.routing.message.handler.NodeLookupHandler;
import eco.data.m3.routing.message.handler.StoreContentHandler;

/**
 * @author xquan
 *
 */
public class MSingleHost {
	
	private MNode node;
	
	private MessageFactory messageFactory = new MessageFactory();
	
	private static MSingleHost _instance;
	
	public static MSingleHost getInstance() {
		if(_instance==null) {
			_instance = new MSingleHost();
		}
		return _instance;
	}
	
	private MSingleHost() {
		messageFactory.registMessage(MessageCode.ACKNOWLEDGE, AcknowledgeMessage.class);
		messageFactory.registMessage(MessageCode.CONNECT, ConnectMessage.class);
		messageFactory.registMessage(MessageCode.CONTENT_LOOKUP, ContentLookupMessage.class);
		messageFactory.registMessage(MessageCode.CONTENT, ContentMessage.class);
		messageFactory.registMessage(MessageCode.NODE_LOOKUP, NodeLookupMessage.class);
		messageFactory.registMessage(MessageCode.NODE_REPLY, NodeReplyMessage.class);
		messageFactory.registMessage(MessageCode.STORE_CONTENT, StoreContentMessage.class);
		messageFactory.registMessage(MessageCode.STORE_CONTENT_REPLY, StoreContentReplyMessage.class);

		messageFactory.registHandler(MessageCode.CONNECT, ConnectHandler.class);
		messageFactory.registHandler(MessageCode.CONTENT_LOOKUP, ContentLookupHandler.class);
		messageFactory.registHandler(MessageCode.NODE_LOOKUP, NodeLookupHandler.class);
		messageFactory.registHandler(MessageCode.STORE_CONTENT, StoreContentHandler.class);
	}
    
    public void initWithMixNode(MId mid, String deviceInfo, String rootPath) throws SocketException {
    	if(this.node!=null) return;
    	
		ServerConfig config = new ServerConfig();		
		config.setNetType(ServerNetType.MIX);
		config.setMessageFactory(messageFactory);
		config.setDeviceInfo(deviceInfo);
		config.setMid(mid);

		MConfiguration mconf = new MConfiguration();
		mconf.setRootPath(rootPath);
    	this.node = new MNode(config, mconf);
    }
    
    public void initWithUDPNode(MId mid, String deviceInfo, String staticIp, int port) throws SocketException {
    	if(this.node!=null) return;
    	
		ServerConfig config = new ServerConfig();		
		config.setNetType(ServerNetType.UDP);
		config.setMessageFactory(messageFactory);
		config.setDeviceInfo(deviceInfo);
		config.setMid(mid);
		config.setStaticIp(staticIp);
		config.setPort(port);
    	this.node = new MNode(config, new MConfiguration());
	}

	public MNode getNode() {
		return node;
	}
	
	public void shutdown(boolean saveState) throws IOException {
		node.shutdown(saveState);
	}
	
}
