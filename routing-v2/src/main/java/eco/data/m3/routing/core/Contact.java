package eco.data.m3.routing.core;

import eco.data.m3.net.core.MId;

/**
 * Keeps information about contacts of the Node
 * Contacts are stored in the Buckets in the Routing Table.
 * 
 * @author xquan
 *
 */
public class Contact implements Comparable<Contact>{


    private final MId n;
    private long lastSeen;

    /**
     * Stale as described by Kademlia paper page 64
     * When a contact fails to respond, if the replacement cache is empty and there is no replacement for the contact,
     * just mark it as stale.
     *
     * Now when a new contact is added, if the contact is stale, it is removed.
     */
    private int staleCount;

    /**
     * Create a contact object
     *
     * @param n The node associated with this contact
     */
    public Contact(MId n)
    {
        this.n = n;
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    public MId getNode()
    {
        return this.n;
    }

    /**
     * When a Node sees a contact a gain, the Node will want to update that it's seen recently,
     * this method updates the last seen timestamp for this contact.
     */
    public void setSeenNow()
    {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    /**
     * When last was this contact seen?
     *
     * @return long The last time this contact was seen.
     */
    public long lastSeen()
    {
        return this.lastSeen;
    }

    @Override
    public boolean equals(Object c)
    {
        if (c instanceof Contact)
        {
            return ((Contact) c).getNode().equals(this.getNode());
        }

        return false;
    }

    /**
     * Increments the amount of times this count has failed to respond to a request.
     */
    public void incrementStaleCount()
    {
        staleCount++;
    }

    /**
     * @return Integer Stale count
     */
    public int staleCount()
    {
        return this.staleCount;
    }

    /**
     * Reset the stale count of the contact if it's recently seen
     */
    public void resetStaleCount()
    {
        this.staleCount = 0;
    }

    @Override
    public int compareTo(Contact o)
    {
        if (this.getNode().equals(o.getNode()))
        {
            return 0;
        }

        return (this.lastSeen() > o.lastSeen()) ? 1 : -1;
    }

    @Override
    public int hashCode()
    {
        return this.getNode().hashCode();
    }

}
