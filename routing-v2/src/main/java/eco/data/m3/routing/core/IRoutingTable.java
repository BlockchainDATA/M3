package eco.data.m3.routing.core;

import java.util.List;

import eco.data.m3.content.data.Contact;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MConfiguration;

public interface IRoutingTable {

    /**
     * Initialize the RoutingTable to it's default state
     */
    public void initialize();

    /**
     * Sets the configuration file for this routing table
     *
     * @param config
     */
    public void setConfiguration(MConfiguration config);

    /**
     * Adds a contact to the routing table based on how far it is from the LocalNode.
     *
     * @param c The contact to add
     */
    public void insert(Contact c);
	
	public void save() ;

    /**
     * Adds a node to the routing table based on how far it is from the LocalNode.
     *
     * @param n The node to add
     */
    public void insert(MId n);

    /**
     * Find the closest set of contacts to a given NodeId
     *
     * @param target           The NodeId to find contacts close to
     * @param numNodesRequired The number of contacts to find
     *
     * @return List A List of contacts closest to target
     */
    public List<MId> findClosest(MId target, int numNodesRequired);

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public List<MId> getAllNodes();

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public List<Contact> getAllContacts();

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param contacts The set of unresponsive contacts
     */
    public void setUnresponsiveContacts(List<MId> contacts);

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param n
     */
    public void setUnresponsiveContact(MId n);
    

    /**
     * Compute the bucket ID in which a given node should be placed; the bucketId is computed based on how far the node is away from the Local Node.
     *
     * @param nid The NodeId for which we want to find which bucket it belong to
     *
     * @return Integer The bucket ID in which the given node should be placed.
     */
    public int getBucketId(MId mid);
}
