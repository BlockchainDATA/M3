package eco.data.m3.routing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import data.eco.net.p2p.message.MessageFactory;
import eco.data.m3.net.core.MId;
import eco.data.m3.net2.NetServiceConfig;
import eco.data.m3.routing.exception.MIdAlreadyExistException;

public class MHost {

	private LinkedHashMap<String, MNode> nodeMap = new LinkedHashMap<>();

	private MessageFactory messageFactory = new DefaultMessageFactory();

	private int portBegin = 9000;

	public MHost() {
	}

	public MNode createNode(String name, MId mid) throws Exception {
		return createNode(name, mid, new MConfiguration());
	}
	
	public MNode createNode(String name, MId mid, MConfiguration mconf) throws Exception {
		NetServiceConfig sc = new NetServiceConfig(mid);
		sc.setFactory(messageFactory);
		sc.setEnableStaticTCP(false);
		sc.setTcpPort(portBegin);
		sc.setMtpPort(portBegin+50);
		sc.setPortMin(portBegin);
		sc.setPortMax(portBegin+99);
		sc.setNamingServerAddr("47.98.102.140");
		portBegin+=100;
		
		return createNode(name, sc, mconf);
	}

	public MNode createAndroidNode(String name, MId mid, String rootPath) throws Exception {
		MConfiguration conf = new MConfiguration();
		conf.setRootPath(rootPath);
		conf.setOnAndroid(true);
		return createNode(name, mid, conf);
	}

	public MNode createNode(String name, NetServiceConfig config) throws Exception {
		config.setFactory(messageFactory);
		return createNode(name, config, new MConfiguration());
	}

	public MNode createNode(String name, NetServiceConfig netConfig, MConfiguration node_config)
			throws Exception {
		if (nodeMap.get(name) != null)
			throw new MIdAlreadyExistException();
		if (netConfig.getFactory() == null)
			netConfig.setFactory(messageFactory);

		MNode node = new MNode(netConfig, node_config);
		nodeMap.put(name, node);
		return node;
	}

	public Collection<MNode> listNodes() {
		return nodeMap.values();
	}

	public MNode getNode(String name) {
		return nodeMap.get(name);
	}

	public void shutdownNode(MNode node) throws IOException {
		node.shutdown();
		nodeMap.remove(node.getName());
	}
	public void shutdownNode(String name) throws IOException {
		nodeMap.get(name);
		nodeMap.remove(name);
	}

	public void shutdownAllNodes(boolean saveState) throws IOException {
		List<MNode> nodes = new ArrayList<>(nodeMap.values());
		for (MNode node : nodes) {
			node.shutdown();
		}
	}
	
	public int getPortBegin() {
		return portBegin;
	}

	public void setPortBegin(int port_begin) {
		this.portBegin = port_begin;
	}
}
