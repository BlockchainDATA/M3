package eco.data.m3.net.lookup;

import java.net.SocketAddress;

import eco.data.m3.net.core.MId;

/**
 * MId to Network Address Lookuper Interface.
 * 
 * @author xquan
 *
 */
public interface ILookuper {
	
	public void init();

	public SocketAddress lookup(MId mid) ;
	
}
