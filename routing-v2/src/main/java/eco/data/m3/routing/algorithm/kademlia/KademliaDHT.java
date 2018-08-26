package eco.data.m3.routing.algorithm.kademlia;

import eco.data.m3.routing.core.Configuration;
import eco.data.m3.routing.core.DHT;

public class KademliaDHT extends DHT {

	public KademliaDHT(String ownerId, Configuration config) {
		super(ownerId, config);
	}

}
