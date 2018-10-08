package eco.data.m3.routing.algorithm.kademlia;

import eco.data.m3.net.core.KeyComparator;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.core.Contact;
import eco.data.m3.routing.core.IRoutingTable;
import eco.data.m3.routing.core.MConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Implementation of a Kademlia routing table
 * 
 * @author xquan
 *
 */
public class KademliaRoutingTable implements IRoutingTable {
    private final MId nodeId;  // The current node
    private transient Bucket[] buckets;

    private transient MConfiguration config;
	
	public KademliaRoutingTable(MId nodeId, MConfiguration config) {
		this.nodeId = nodeId;
		this.config = config;

        /* Initialize all of the buckets to a specific depth */
		initialize();
		
        /* Insert the local node */
		insert(nodeId);
	}

	@Override
	public void initialize() {
        this.buckets = new Bucket[MId.ID_LENGTH];
        for (int i = 0; i < MId.ID_LENGTH; i++)
        {
            buckets[i] = new Bucket(i, this.config);
        }		
	}

	@Override
	public void setConfiguration(MConfiguration config) {
        this.config = config;		
	}

	@Override
	public synchronized void insert(Contact c) {
        this.buckets[this.getBucketId(c.getNode())].insert(c);
	}

	@Override
	public void insert(MId n) {
        this.buckets[this.getBucketId(n)].insert(n);
	}

	@Override
	public List<MId> findClosest(MId target, int numNodesRequired) {
        TreeSet<MId> sortedSet = new TreeSet<>(new KeyComparator(target));
        sortedSet.addAll(this.getAllNodes());

        List<MId> closest = new ArrayList<>(numNodesRequired);

        /* Now we have the sorted set, lets get the top numRequired */
        int count = 0;
        for (MId n : sortedSet)
        {
            closest.add(n);
            if (++count == numNodesRequired)
            {
                break;
            }
        }
        return closest;
	}

	@Override
	public List<MId> getAllNodes() {
        List<MId> nodes = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            for (Contact c : b.getContacts())
            {
                nodes.add(c.getNode());
            }
        }

        return nodes;
	}

	@Override
	public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            contacts.addAll(b.getContacts());
        }

        return contacts;
	}

	@Override
	public void setUnresponsiveContacts(List<MId> contacts) {
        if (contacts.isEmpty())
        {
            return;
        }
        for (MId n : contacts)
        {
            this.setUnresponsiveContact(n);
        }
	}

	@Override
	public void setUnresponsiveContact(MId n) {
        int bucketId = this.getBucketId(n);

        /* Remove the contact from the bucket */
        this.buckets[bucketId].removeNode(n);
	}

	@Override
	public int getBucketId(MId mid) {
        int bId = (this.nodeId).getDistance(mid) - 1;

        /* If we are trying to insert a node into it's own routing table, then the bucket ID will be -1, so let's just keep it in bucket 0 */
        return bId < 0 ? 0 : bId;
	}

    @Override
    public synchronized final String toString()
    {
        StringBuilder sb = new StringBuilder("\nPrinting Routing Table Started ***************** \n");
        int totalContacts = 0;
        for (Bucket b : this.buckets)
        {
            if (b.numContacts() > 0)
            {
                totalContacts += b.numContacts();
                sb.append("# nodes in Bucket with depth ");
                sb.append(b.getDepth());
                sb.append(": ");
                sb.append(b.numContacts());
                sb.append("\n");
                sb.append(b.toString());
                sb.append("\n");
            }
        }

        sb.append("\nTotal Contacts: ");
        sb.append(totalContacts);
        sb.append("\n\n");

        sb.append("Printing Routing Table Ended ******************** ");

        return sb.toString();
    }

}
