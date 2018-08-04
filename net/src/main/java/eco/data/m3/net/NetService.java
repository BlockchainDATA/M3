package eco.data.m3.net;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.exception.MIdAlreadyExistException;
import eco.data.m3.net.server.Server;
import eco.data.m3.net.server.ServerConfig;
import eco.data.m3.net.server.udp.UDPServer;

/**
 * net library's main interface.
 * 
 * @author xquan
 *
 */
public class NetService {

	private static NetService instance;
	
	private HashMap<MId, Server> serverMap = new HashMap<>();

	public static NetService getInstance() {
		if (instance == null) {
			instance = new NetService();
		}
		return instance;
	}

	private NetService() {
	}
	
	public Server createServer(ServerConfig config, MId mid) throws SocketException, MIdAlreadyExistException {
		Server server = getServer(mid);
		if(server!=null)
			throw new MIdAlreadyExistException();
		
		switch (config.getNetType()) {
		case UDP:
			server = new UDPServer(config, mid);
		case TCP:			
			break;
		default:           
			break;
		}
		if(null != server) {
			serverMap.put(mid, server);
		}
		return server;		
	}
	
	public void destroyServer(Server server) {
		server.shutdown();
		serverMap.remove(server.getMId());
	}
	
	public void destroyServer(MId mid) {
		Server server = getServer(mid);
		if(null != server) {
			destroyServer(server);
		}
	}
	
	public Server getServer(MId mid) {
		return serverMap.get(mid);
	}
	
	public List<Server> getServers(){
		return (List<Server>) serverMap.values();
	}

}
