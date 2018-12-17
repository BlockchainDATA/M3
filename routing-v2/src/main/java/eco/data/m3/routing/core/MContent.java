package eco.data.m3.routing.core;

import eco.data.m3.net.core.MId;

/**
 * Basic DHT Content object to test DHT storage
 * @author xquan
 *
 */
public class MContent {

    public static final transient int TYPE = 0x06;

    private MId key;
    private byte [] data;
    private MId ownerId;
    private final long createTs = System.currentTimeMillis() / 1000L;
    private long updateTs = createTs;

    public MContent(MId ownerId, String content)
    {
        this.ownerId = ownerId;
        this.key = new MId();
        setData(content.getBytes());
    }

    public MContent(MId ownerId, MId key, String content)
    {
        this.ownerId = ownerId;
        this.key = new MId();
        setData(content.getBytes());
    }


    public MContent(MId ownerId, byte [] content)
    {
        this.ownerId = ownerId;
        this.key = new MId();
        setData(content);
    }

    public MContent(MId ownerId, MId key)
    {
        this.ownerId = ownerId;
        this.key = key;
    }

    public void setData(byte [] newData)
    {
        this.data = newData;
        this.setUpdated();
    }
    
    public byte [] getData()
    {
    	return this.data;
    }

    /**
     * @return NodeId The DHT key for this content
     */
    public MId getKey() {
        return this.key;
	}

    /**
     * @return String The type of content
     */
    public int getType() {
    	return TYPE;
	}

    /**
     * Each content will have an created date
     * This allows systems to know when to delete a content form his/her machine
     *
     * @return long The create date of this content
     */
    public long getCreatedTimestamp() {
        return this.createTs;
	}

    /**
     * Each content will have an update timestamp
     * This allows the DHT to keep only the latest version of a content
     *
     * @return long The timestamp of when this content was last updated
     */
    public long getLastUpdatedTimestamp() {
        return this.updateTs;
	}
    
    /**
     * Set the content as updated
     */
    public void setUpdated()
    {
        this.updateTs = System.currentTimeMillis() / 1000L;
    }

    /**
     * Each content needs to be in byte format for transporting and storage,
     * this method takes care of that.
     *
     * Each object is responsible for transforming itself to byte format since the
     * structure of methods may differ.
     *
     * @return The content in byte format
     */
    public byte[] toSerializedForm() {
    	return data;
	}

    /**
     * Given the Content in byte format, read it
     *
     * @param data The object in byte format
     *
     * @return A new object from the given
     */
    public static MContent fromSerializedForm(byte[] data) {
    	MContent c = new MContent(new MId(), data);
        return c;
	}

	public MId getOwnerId() {
		return ownerId;
	}

}
