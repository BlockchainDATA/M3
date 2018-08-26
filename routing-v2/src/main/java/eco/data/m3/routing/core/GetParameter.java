package eco.data.m3.routing.core;

import eco.data.m3.net.core.MId;

public class GetParameter {

    private MId key;
    private String ownerId = null;
    private String type = null;

    /**
     * Construct a GetParameter to search for data by NodeId and owner
     *
     * @param key
     * @param type
     */
    public GetParameter(MId key, String type)
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
    public GetParameter(MId key, String type, String owner)
    {
        this(key, type);
        this.ownerId = owner;
    }

    /**
     * Construct our get parameter from a Content
     *
     * @param c
     */
    public GetParameter(Content c)
    {
        this.key = c.getKey();

        if (c.getType() != null)
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

        if (md.getType() != null)
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

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId()
    {
        return this.ownerId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }

    @Override
    public String toString()
    {
        return "GetParameter - [Key: " + key + "][Owner: " + this.ownerId + "][Type: " + this.type + "]";
    }
}
