package eco.data.m3.routing.core;

import java.io.File;

public class MConfiguration {

    private final static long RESTORE_INTERVAL = 60 * 1000; // in milliseconds
    private final static long RESPONSE_TIMEOUT = 100000;
    private final static long OPERATION_TIMEOUT = 100000;
    private final static int CONCURRENCY = 10;
    private final static int K = 5;
    private final static int RCSIZE = 3;
    private final static int STALE = 1;
    private final static String LOCAL_FOLDER = "mnode";
    
    private final static boolean IS_TESTING = false;
    
    private DHTType dhtType = DHTType.Kademlia;

    public long restoreInterval()
    {
        return RESTORE_INTERVAL;
    }

    public long responseTimeout()
    {
        return RESPONSE_TIMEOUT;
    }

    public long operationTimeout()
    {
        return OPERATION_TIMEOUT;
    }

    public int maxConcurrentMessagesTransiting()
    {
        return CONCURRENCY;
    }

    public int k()
    {
        return K;
    }

    public int replacementCacheSize()
    {
        return RCSIZE;
    }
    
    public int stale()
    {
        return STALE;
    }

    public String getNodeDataFolder(String ownerId)
    {
        /* Setup the main storage folder if it doesn't exist */
        String path = System.getProperty("user.home") + File.separator + MConfiguration.LOCAL_FOLDER;
        File folder = new File(path);
        if (!folder.isDirectory())
        {
            folder.mkdir();
        }

        /* Setup subfolder for this owner if it doesn't exist */
        File ownerFolder = new File(folder + File.separator + ownerId);
        if (!ownerFolder.isDirectory())
        {
            ownerFolder.mkdir();
        }

        /* Return the path */
        return ownerFolder.toString();
    }

    public boolean isTesting()
    {
        return IS_TESTING;
    }

	public DHTType getDhtType() {
		return dhtType;
	}

	public void setDhtType(DHTType dhtType) {
		this.dhtType = dhtType;
	}
}
