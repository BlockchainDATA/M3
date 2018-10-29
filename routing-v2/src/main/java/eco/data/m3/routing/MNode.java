package eco.data.m3.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import eco.data.m3.naming.rpc.async.NamingAsyncClient;
import eco.data.m3.net.NetService;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.exception.MIdAlreadyExistException;
import eco.data.m3.net.server.Server;
import eco.data.m3.net.server.ServerConfig;
import eco.data.m3.routing.algorithm.kademlia.KademliaDHT;
import eco.data.m3.routing.algorithm.kademlia.KademliaRoutingTable;
import eco.data.m3.routing.core.DHT;
import eco.data.m3.routing.core.DHTType;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.core.IRoutingTable;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.core.MContent;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.exception.ContentNotFoundException;
import eco.data.m3.routing.operation.ConnectOperation;
import eco.data.m3.routing.operation.ContentLookupOperation;
import eco.data.m3.routing.operation.IOperation;
import eco.data.m3.routing.operation.RefreshOperation;
import eco.data.m3.routing.operation.StoreOperation;
import eco.data.m3.routing.serializer.JsonDHTSerializer;
import eco.data.m3.routing.serializer.JsonRoutingTableSerializer;
import eco.data.m3.routing.serializer.JsonSerializer;

/**
 * @author xquan
 *
 */
public class MNode {

    /* Objects to be used */
    private transient MId parentId;
    private final transient Server server;
    private final transient DHT dht;
    private transient IRoutingTable routingTable;
    private transient MConfiguration config;

    /* Timer used to execute refresh operations */
    private transient Timer refreshOperationTimer;
    private transient TimerTask refreshOperationTTask;

    /* Statistics */
    private final transient Statistician statistician = new Statistician();

    /**
     * Creates a Kademlia DistributedMap using the specified name as filename base.
     * If the id cannot be read from disk the specified defaultId is used.
     * The instance is bootstraped to an existing network by specifying the
     * address of a bootstrap node in the network.
     *
     * @param ownerId      The Name of this node used for storage
     * @param localNode    The Local Node for this Kad instance
     * @param udpPort      The UDP port to use for routing messages
     * @param dht          The DHT for this instance
     * @param config
     * @param routingTable
     *
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to bootstrap to the network
     * */    
    public MNode(ServerConfig server_config, MConfiguration node_config) throws SocketException {
        this.config = node_config;
        MId nodeId = server_config.getMid();
        
        if(node_config.getDhtType()==DHTType.Kademlia) {
	        this.routingTable = new KademliaRoutingTable(server_config.getMid(), node_config);
	    	this.dht = new KademliaDHT(server_config.getMid().toString(), config);
        }else {
        	this.routingTable = null;
        	this.dht = null;
        }
    	this.server = NetService.getInstance().createServer(server_config);
    	this.server.setData(this);
    	
    	startRefreshOperation();
    }

    public MNode(DHT dht, IRoutingTable rt, ServerConfig server_config, MConfiguration node_config) throws MIdAlreadyExistException, SocketException {
    	this.routingTable = rt;
    	this.dht = dht;
        this.config = node_config;

    	this.server = NetService.getInstance().createServer(server_config);
    	this.server.setData(this);
    	
    	startRefreshOperation();
    }
    

    public final void startRefreshOperation()
    {
        this.refreshOperationTimer = new Timer(true);
        refreshOperationTTask = new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    /* Runs a DHT RefreshOperation  */
                    MNode.this.refresh();
                }
                catch (Throwable e)
                {
                    System.err.println("KademliaNode: Refresh Operation Failed; Message: " + e.getMessage());
                }
            }
        };
        refreshOperationTimer.schedule(refreshOperationTTask, this.config.restoreInterval(), this.config.restoreInterval());
    }

    public final void stopRefreshOperation()
    {
        /* Close off the timer tasks */
        this.refreshOperationTTask.cancel();
        this.refreshOperationTimer.cancel();
        this.refreshOperationTimer.purge();
    }

    /**
     * Load Stored state using default configuration
     *
     * @param ownerId The ID of the owner for the stored state
     *
     * @return A Kademlia instance loaded from a stored state in a file
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static MNode loadFromFile(String ownerId, ServerConfig serverConfig) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        return MNode.loadFromFile(ownerId, serverConfig, new MConfiguration());
    }

    public Server getServer()
    {
        return this.server;
    }

    public DHT getDHT()
    {
        return this.dht;
    }

    public MConfiguration getCurrentConfiguration()
    {
        return this.config;
    }

    public synchronized final void join(MId n) throws Throwable
    {
    	this.setParentId(n);
        long startTime = System.nanoTime();
        IOperation op = new ConnectOperation(this, n);
        op.execute();
        long endTime = System.nanoTime();
        this.statistician.setBootstrapTime(endTime - startTime);
    }

    public List<MId> putContent(MContent content) throws Throwable
    {
        return this.put(new StorageEntry(content));
    }

    public List<MId> put(StorageEntry entry) throws Throwable
    {
        StoreOperation sop = new StoreOperation(this, entry);
        sop.execute();

        /* Return how many nodes the content was stored on */
        return sop.getNodesStoredAt();
    }

    public void putLocally(MContent content) throws IOException
    {
        this.dht.store(new StorageEntry(content));
    }
    
    public MContent getContent(GetParameter param) throws NoSuchElementException, IOException, ContentNotFoundException{
		return null;    	
    }

    public StorageEntry get(GetParameter param) throws Throwable
    {
        if (this.dht.contains(param))
        {
            /* If the content exist in our own DHT, then return it. */
            return this.dht.get(param);
        }

        /* Seems like it doesn't exist in our DHT, get it from other Nodes */
        long startTime = System.nanoTime();
        ContentLookupOperation clo = new ContentLookupOperation(this, param);
        clo.execute();
        long endTime = System.nanoTime();
        this.statistician.addContentLookup(endTime - startTime, clo.routeLength(), clo.isContentFound());
        return clo.getContentFound();
    }

    public void refresh() throws Throwable
    {
        new RefreshOperation(this).execute();
    }

    public String getDeviceInfo()
    {
        return NamingAsyncClient.PEER_INFO.deviceInfo;
    }

    public void shutdown(final boolean saveState) throws IOException
    {
        /* Shut down the server */
        NetService.getInstance().destroyServer();

        this.stopRefreshOperation();

        /* Save this Kademlia instance's state if required */
        if (saveState)
        {
            /* Save the system state */
            this.saveState();
        }
    }

    /**
     * Load Stored state
     *
     * @param ownerId The ID of the owner for the stored state
     * @param iconfig Configuration information to work with
     *
     * @return A Kademlia instance loaded from a stored state in a file
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static MNode loadFromFile(String ownerId, ServerConfig serverConfig, MConfiguration nodeConfig) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        DataInputStream din;

        /**
         * @section Read Basic Kad data
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, nodeConfig) + File.separator + "kad.kns"));
        MNode ikad = new JsonSerializer<MNode>().read(din);

        /**
         * @section Read the routing table
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, nodeConfig) + File.separator + "routingtable.kns"));
        IRoutingTable rt = new JsonRoutingTableSerializer(nodeConfig).read(din);

        /**
         * @section Read the node state
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, nodeConfig) + File.separator + "node.kns"));
        MId nodeId = new JsonSerializer<MId>().read(din);

        /**
         * @section Read the DHT
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, nodeConfig) + File.separator + "dht.kns"));
        DHT dht = new JsonDHTSerializer().read(din);
        dht.setConfiguration(nodeConfig);
        
        serverConfig.setMid(MId.fromHexString(ownerId));

        return new MNode(dht, rt, serverConfig, nodeConfig);
    } 

    public void saveState() throws IOException
    {
        DataOutputStream dout;
        
        String folderName = getNodeId().toString();

        /**
         * @section Store Basic Kad data
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(folderName, this.config) + File.separator + "kad.kns"));
        new JsonSerializer<MNode>().write(this, dout);

        /**
         * @section Save the node state
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(folderName, this.config) + File.separator + "node.kns"));
        new JsonSerializer<MId>().write(getNodeId(), dout);

        /**
         * @section Save the routing table
         * We need to save the routing table separate from the node since the routing table will contain the node and the node will contain the routing table
         * This will cause a serialization recursion, and in turn a Stack Overflow
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(folderName, this.config) + File.separator + "routingtable.kns"));
        new JsonRoutingTableSerializer(this.config).write(this.getRoutingTable(), dout);

        /**
         * @section Save the DHT
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(folderName, this.config) + File.separator + "dht.kns"));
        new JsonDHTSerializer().write(this.dht, dout);

    }

    /**
     * Get the name of the folder for which a content should be stored
     *
     * @return String The name of the folder to store node states
     */
    private static String getStateStorageFolderName(String name, MConfiguration iconfig)
    {
        /* Setup the nodes storage folder if it doesn't exist */
        String path = iconfig.getNodeDataFolder(name) + File.separator + "nodeState";
        File nodeStateFolder = new File(path);
        if (!nodeStateFolder.isDirectory())
        {
            nodeStateFolder.mkdir();
        }
        return nodeStateFolder.toString();
    }

    public IRoutingTable getRoutingTable()
    {
        return this.routingTable;
    }

    public Statistician getStatistician()
    {
        return this.statistician;
    }

    /**
     * Creates a string containing all data about this Kademlia instance
     *
     * @return The string representation of this Kad instance
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\n\nPrinting Kad State for instance with owner: ");
        sb.append(this.getDeviceInfo());
        sb.append("\n\n");

        sb.append("\n");
        sb.append("Local Node");
        sb.append(this.getNodeId());
        sb.append("\n");

        sb.append("\n");
        sb.append("Routing Table: ");
        sb.append(this.getRoutingTable());
        sb.append("\n");

        sb.append("\n");
        sb.append("DHT: ");
        sb.append(this.dht);
        sb.append("\n");

        sb.append("\n\n\n");

        return sb.toString();
    }

	public MId getNodeId() {
		return MId.fromHexString(NamingAsyncClient.PEER_INFO.murl);
	}

	public MId getParentId() {
		return parentId;
	}

	public void setParentId(MId parentId) {
		this.parentId = parentId;
	} 

}
