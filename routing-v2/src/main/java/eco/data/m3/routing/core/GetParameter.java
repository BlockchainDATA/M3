package eco.data.m3.routing.core;

import eco.data.m3.net.core.MId;

public class GetParameter {

    private MId key;
    private MId ownerId = null;
    private int type = 0;

    /**
     * Construct a GetParameter to search for data by NodeId and owner
     *
     * @param key
     * @param type
     */
    public GetParameter(MId key, int type)
    {
        this.key = key;
        this.type = type;
    }

    /**
     * Construct a GetParameter to search for data by NodeId, owner, type
     *
     * @param key
     * @param type
     * @param owner
     */
    public GetParameter(MId key, int type, MId owner)
    {
        this(key, type);
        this.ownerId = owner;
    }

    /**
     * Construct our get parameter from a Content
     *
     * @param c
     */
    public GetParameter(MContent c)
    {
        this.key = c.getKey();

        if (c.getType() != 0)
        {
            this.type = c.getType();
        }

        if (c.getOwnerId() != null)
        {
            this.ownerId = c.getOwnerId();
        }
    }

    /**
     * Construct our get parameter from a StorageEntryMeta data
     *
     * @param md
     */
    public GetParameter(StorageEntryMetadata md)
    {
        this.key = md.getKey();

        if (md.getType() != 0)
        {
            this.type = md.getType();
        }

        if (md.getOwnerId() != null)
        {
            this.ownerId = md.getOwnerId();
        }
    }

    public MId getKey()
    {
        return this.key;
    }

    public void setOwnerId(MId ownerId)
    {
        this.ownerId = ownerId;
    }

    public MId getOwnerId()
    {
        return this.ownerId;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return this.type;
    }

    @Override
    public String toString()
    {
        return "GetParameter - [Key: " + key + "][Owner: " + this.ownerId + "][Type: " + this.type + "]";
    }
}
