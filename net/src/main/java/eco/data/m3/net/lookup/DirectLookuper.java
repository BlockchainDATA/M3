package eco.data.m3.net.lookup;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import eco.data.m3.net.NetService;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.server.Server;

/**
 * This is a simple Network Address lookuper implementation.
 * 
 * @author xquan
 *
 */
public class DirectLookuper implements ILookuper{

	@Override
	public SocketAddress lookup(MId mid) {
		Server server = NetService.getInstance().getServer(mid);
		if(server==null)
			return null;
		SocketAddress addr;
		try {
			addr = new InetSocketAddress(InetAddress.getByName("localhost"), server.getPort());
			return addr;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void init() {
		//Do Nothing
	}

}
