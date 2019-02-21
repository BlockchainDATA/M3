package eco.data.m3.routing;

import java.io.File;

public class MConfiguration {

    private final static long RESTORE_INTERVAL = 10 * 1000; // in milliseconds
    private final static long OPERATION_TIMEOUT = 10 * 1000;
    private final static long PERSISTANCE_INTERVAL = 60 * 1000;
    private final static int CONCURRENCY = 10;
    private final static int K = 5;
    private final static int RCSIZE = 3;
    private final static int STALE = 1;
    private final static String LOCAL_FOLDER = "mnode";
    
    private final static boolean IS_TESTING = false;

    private String rootPath = null;
    
    private boolean isOnAndroid = false;

    public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public long restoreInterval()
    {
        return RESTORE_INTERVAL;
    }

	public long persistanceInterval()
    {
        return PERSISTANCE_INTERVAL;
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
        String path = null;
        
        if(rootPath!=null)
            path = rootPath;
        else
            path = System.getProperty("user.home") + File.separator + MConfiguration.LOCAL_FOLDER;

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

	public boolean isOnAndroid() {
		return isOnAndroid;
	}

	public void setOnAndroid(boolean isOnAndroid) {
		this.isOnAndroid = isOnAndroid;
	}
    
}
