package eco.data.m3.routing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.exception.MIdAlreadyExistException;
import eco.data.m3.net.message.MessageFactory;
import eco.data.m3.net.server.ServerConfig;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.core.NodeSetting;
import eco.data.m3.routing.message.AcknowledgeMessage;
import eco.data.m3.routing.message.ConnectMessage;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentMessage;
import eco.data.m3.routing.message.MessageCode;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.handler.ConnectHandler;
import eco.data.m3.routing.message.handler.ContentLookupHandler;
import eco.data.m3.routing.message.handler.NodeLookupHandler;
import eco.data.m3.routing.message.handler.StoreContentHandler;

/**
 * @author xquan
 *
 */
public class MHost {
	
	private LinkedHashMap<String, MNode> nodeMap = new LinkedHashMap<>();
	
	private MessageFactory messageFactory = new MessageFactory();
	
	public MHost() {
		messageFactory.registMessage(MessageCode.ACKNOWLEDGE, AcknowledgeMessage.class);
		messageFactory.registMessage(MessageCode.CONNECT, ConnectMessage.class);
		messageFactory.registMessage(MessageCode.CONTENT_LOOKUP, ContentLookupMessage.class);
		messageFactory.registMessage(MessageCode.CONTENT, ContentMessage.class);
		messageFactory.registMessage(MessageCode.NODE_LOOKUP, NodeLookupMessage.class);
		messageFactory.registMessage(MessageCode.NODE_REPLY, NodeReplyMessage.class);
		messageFactory.registMessage(MessageCode.STORE_CONTENT, StoreContentMessage.class);

		messageFactory.registHandler(MessageCode.CONNECT, ConnectHandler.class);
		messageFactory.registHandler(MessageCode.CONTENT_LOOKUP, ContentLookupHandler.class);
		messageFactory.registHandler(MessageCode.NODE_LOOKUP, NodeLookupHandler.class);
		messageFactory.registHandler(MessageCode.STORE_CONTENT, StoreContentHandler.class);
	}
	
	public MNode createNode(String name, MId mid) throws MIdAlreadyExistException, SocketException {
		ServerConfig sc = new ServerConfig();
		sc.setMessageFactory(messageFactory);
		return createNode(name, mid, sc, new MConfiguration());
	}
	
	public MNode createNode(String name, MId mid, ServerConfig config) throws MIdAlreadyExistException, SocketException {
		config.setMessageFactory(messageFactory);
		return createNode(name, mid, config, new MConfiguration());
	}
	
	public MNode createNode(String name, MId mid, ServerConfig server_config, MConfiguration node_config) throws MIdAlreadyExistException, SocketException {
		if(nodeMap.get(name)!=null)
			throw new MIdAlreadyExistException();
		if(server_config.getMessageFactory()==null)
			server_config.setMessageFactory(messageFactory);
		
		MNode node = new MNode(name, mid, server_config, node_config);
		nodeMap.put(name, node);
		return node;
	}
	
	public Collection<MNode> listNodes(){
		return nodeMap.values();
	}
	
	public MNode getNode(String name) {
		return nodeMap.get(name);
	}
	
	public void shutdownNode(MNode node, boolean saveState) throws IOException {
		node.shutdown(saveState);
		nodeMap.remove(node.getName());
	}
	
	public MNode loadFromFile(String nodeName) throws FileNotFoundException, ClassNotFoundException, IOException {
		ServerConfig sc = new ServerConfig();
		sc.setMessageFactory(messageFactory);
		MNode node = MNode.loadFromFile(nodeName, sc);
		nodeMap.put(nodeName, node);
		return node;		
	}
	
	public void joinHostNodes(List<NodeSetting> settings) throws FileNotFoundException, ClassNotFoundException, IOException {
		//Join
		for (NodeSetting nodeSetting : settings) {
			MNode node = getNode(nodeSetting.getName());
			if(nodeSetting.getParentId()!=null) {
				node.join(new MId(nodeSetting.getParentId()));
			}
		}
	}
	
	public void shutdownAllNodes(boolean saveState) throws IOException {
		List<MNode> nodes = new ArrayList<>(nodeMap.values());
		for (MNode mNode : nodes) {
			shutdownNode(mNode, saveState);
		}
	}

}
