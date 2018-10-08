package eco.data.m3.routing;

import eco.data.m3.net.core.MId;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.core.MContent;
import eco.data.m3.routing.core.StorageEntryMetadata;
import eco.data.m3.routing.exception.ContentExistException;
import eco.data.m3.routing.exception.ContentNotFoundException;

import java.util.*;

/**
 * It would be infeasible to keep all content in memory to be send when requested
 * Instead we store content into files
 * We use this Class to keep track of all content stored
 * 
 * @author xquan
 *
 */
public class ContentManager {

    private final Map<MId, List<StorageEntryMetadata>> entries = new HashMap<>();

    /**
     * Add a new entry to our storage
     *
     * @param content The content to store a reference to
     */
    public StorageEntryMetadata put(MContent content) throws ContentExistException
    {
        return this.put(new StorageEntryMetadata(content));
    }

    /**
     * Add a new entry to our storage
     *
     * @param entry The StorageEntry to store
     */
    public StorageEntryMetadata put(StorageEntryMetadata entry) throws ContentExistException
    {
        if (!this.entries.containsKey(entry.getKey()))
        {
            this.entries.put(entry.getKey(), new ArrayList<>());
        }

        /* If this entry doesn't already exist, then we add it */
        if (!this.contains(entry))
        {
            this.entries.get(entry.getKey()).add(entry);

            return entry;
        }
        else
        {
            throw new ContentExistException("Content already exists on this DHT");
        }
    }

    /**
     * Checks if our DHT has a Content for the given criteria
     *
     * @param param The parameters used to search for a content
     *
     * @return boolean
     */
    public synchronized boolean contains(GetParameter param)
    {
        if (this.entries.containsKey(param.getKey()))
        {
            /* Content with this key exist, check if any match the rest of the search criteria */
            for (StorageEntryMetadata e : this.entries.get(param.getKey()))
            {
                /* If any entry satisfies the given parameters, return true */
                if (e.satisfiesParameters(param))
                {
                    return true;
                }
            }
        }
        else
        {
        }
        return false;
    }

    /**
     * Check if a content exist in the DHT
     */
    public synchronized boolean contains(MContent content)
    {
        return this.contains(new GetParameter(content));
    }

    /**
     * Check if a StorageEntry exist on this DHT
     */
    public synchronized boolean contains(StorageEntryMetadata entry)
    {
        return this.contains(new GetParameter(entry));
    }

    /**
     * Checks if our DHT has a Content for the given criteria
     *
     * @param param The parameters used to search for a content
     *
     * @return List of content for the specific search parameters
     */
    public StorageEntryMetadata get(GetParameter param) throws NoSuchElementException
    {
        if (this.entries.containsKey(param.getKey()))
        {
            /* Content with this key exist, check if any match the rest of the search criteria */
            for (StorageEntryMetadata e : this.entries.get(param.getKey()))
            {
                /* If any entry satisfies the given parameters, return true */
                if (e.satisfiesParameters(param))
                {
                    return e;
                }
            }

            /* If we got here, means we didn't find any entry */
            throw new NoSuchElementException();
        }
        else
        {
            throw new NoSuchElementException("No content exist for the given parameters");
        }
    }

    public StorageEntryMetadata get(StorageEntryMetadata md)
    {
        return this.get(new GetParameter(md));
    }

    /**
     * @return A list of all storage entries
     */
    public synchronized List<StorageEntryMetadata> getAllEntries()
    {
        List<StorageEntryMetadata> entriesRet = new ArrayList<>();

        for (List<StorageEntryMetadata> entrySet : this.entries.values())
        {
            if (entrySet.size() > 0)
            {
                entriesRet.addAll(entrySet);
            }
        }

        return entriesRet;
    }

    public void remove(MContent content) throws ContentNotFoundException
    {
        this.remove(new StorageEntryMetadata(content));
    }

    public void remove(StorageEntryMetadata entry) throws ContentNotFoundException
    {
        if (contains(entry))
        {
            this.entries.get(entry.getKey()).remove(entry);
        }
        else
        {
            throw new ContentNotFoundException("This content does not exist in the Storage Entries");
        }
    }

    @Override
    public synchronized String toString()
    {
        StringBuilder sb = new StringBuilder("Stored Content: \n");
        int count = 0;
        for (List<StorageEntryMetadata> es : this.entries.values())
        {
            if (entries.size() < 1)
            {
                continue;
            }

            for (StorageEntryMetadata e : es)
            {
                sb.append(++count);
                sb.append(". ");
                sb.append(e);
                sb.append("\n");
            }
        }

        sb.append("\n");
        return sb.toString();
    }
}
