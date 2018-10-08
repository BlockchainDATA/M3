package eco.data.m3.routing.algorithm.kademlia;

import eco.data.m3.routing.core.DHT;
import eco.data.m3.routing.core.MConfiguration;

public class KademliaDHT extends DHT {

	public KademliaDHT(String ownerId, MConfiguration config) {
		super(ownerId, config);
	}

}
