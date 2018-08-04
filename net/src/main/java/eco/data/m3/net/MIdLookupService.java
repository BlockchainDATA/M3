package eco.data.m3.net;

import java.net.SocketAddress;
import java.net.UnknownHostException;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.lookup.DirectLookuper;
import eco.data.m3.net.lookup.ILookuper;

/**
 * MId to Network Address Lookup Service
 * Get a MId's Server's Socket Address
 * This supply functions like DNS
 * 
 * @author xquan
 *
 */
public class MIdLookupService {

	private static MIdLookupService instance;
	
	private ILookuper lookuper;

	public static MIdLookupService getInstance() {
		if (instance == null) {
			instance = new MIdLookupService();
		}
		return instance;
	}
	
	public void initService() {
		lookuper.init();
	}

	private MIdLookupService() {
		lookuper = new DirectLookuper();
	}
	
	public SocketAddress lookup(MId mid) throws UnknownHostException {
		return lookuper.lookup(mid);
	}

}
