package eco.data.m3.routing.routing;

import java.util.List;
import eco.data.m3.routing.node.Node;

/**
 * A bucket used to store Contacts in the routing table.
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public interface KademliaBucket
{

    /**
     * Adds a contact to the bucket
     *
     * @param c the new contact
     */
    public void insert(Contact c);

    /**
     * Create a new contact and insert it into the bucket.
     *
     * @param n The node to create the contact from
     */
    public void insert(Node n);

    /**
     * Checks if this bucket contain a contact
     *
     * @param c The contact to check for
     *
     * @return boolean
     */
    public boolean containsContact(Contact c);

    /**
     * Checks if this bucket contain a node
     *
     * @param n The node to check for
     *
     * @return boolean
     */
    public boolean containsNode(Node n);

    /**
     * Remove a contact from this bucket.
     * 
     * If there are replacement contacts in the replacement cache,
     * select the last seen one and put it into the bucket while removing the required contact.
     *
     * If there are no contacts in the replacement cache, then we just mark the contact requested to be removed as stale.
     * Marking as stale would actually be incrementing the stale count of the contact.
     *
     * @param c The contact to remove
     *
     * @return Boolean whether the removal was successful.
     */
    public boolean removeContact(Contact c);

    /**
     * Remove the contact object related to a node from this bucket
     *
     * @param n The node of the contact to remove
     *
     * @return Boolean whether the removal was successful.
     */
    public boolean removeNode(Node n);

    /**
     * Counts the number of contacts in this bucket.
     *
     * @return Integer The number of contacts in this bucket
     */
    public int numContacts();

    /**
     * @return Integer The depth of this bucket in the RoutingTable
     */
    public int getDepth();

    /**
     * @return An Iterable structure with all contacts in this bucket
     */
    public List<Contact> getContacts();
}
