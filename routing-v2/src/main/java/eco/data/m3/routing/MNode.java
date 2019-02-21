package eco.data.m3.routing;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.eco.net.p2p.channel.PeerChannel;
import data.eco.net.p2p.channel.PeerLink;
import eco.data.m3.content.MContent;
import eco.data.m3.content.MContentKey;
import eco.data.m3.content.MContentManager;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.core.PeerNode;
import eco.data.m3.net2.NetService;
import eco.data.m3.net2.NetServiceConfig;
import eco.data.m3.net2.NetServiceListener;
import eco.data.m3.routing.algorithm.kademlia.KademliaRoutingTable;
import eco.data.m3.routing.core.IRoutingTable;
import eco.data.m3.routing.core.MDHT;
import eco.data.m3.routing.operation.ConnectOperation;
import eco.data.m3.routing.operation.ContentLookupOperation;
import eco.data.m3.routing.operation.ContentRetrieveOperation;
import eco.data.m3.routing.operation.IOperation;
import eco.data.m3.routing.operation.RefreshOperation;
import eco.data.m3.routing.operation.StoreOperation;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;


/**
 * @author xquan
 *
 */
public class MNode extends PeerNode implements NetServiceListener{

    private static final Logger logger =
            LoggerFactory.getLogger(MNode.class.getName());
    
    /* Objects to be used */
    private transient MId parentId;
    private final transient NetService netService;
    private final transient MDHT dht;
    private transient IRoutingTable routingTable;
    private transient MConfiguration config;
    private MContentManager contentManager;

    /* Timer used to execute refresh operations */    
    private transient ScheduledFuture<?> refreshScheduleTask;
    private transient ScheduledFuture<?> persistanceScheduleTask;

    /* Statistics */
    private final transient Statistician statistician = new Statistician();
    
    private transient NioEventLoopGroup eventLoop = new NioEventLoopGroup();
    
//    private transient NioEventLoopGroup lookupEventLoop = new NioEventLoopGroup(2);

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
     * @throws Exception 
     *
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to bootstrap to the network
     * */    
    public MNode(NetServiceConfig net_config, MConfiguration node_config) throws Exception {
        this.config = node_config;
        MId nodeId = net_config.getMId();

    	this.netService = new NetService(this, net_config);
    	this.netService.addListener(this);

        this.contentManager = new MContentManager(nodeId, config.getRootPath(), config.isOnAndroid());
        this.routingTable = new KademliaRoutingTable(contentManager, node_config);
    	this.dht = new MDHT(contentManager);
    	
    	startScheduleOperation();
    }

    public void startScheduleOperation()
    {
    	logger.debug("startScheduleOperation");
         this.refreshScheduleTask = eventLoop.scheduleAtFixedRate(new Runnable() {			
			@Override
			public void run() {
				logger.debug("******  Refresh : " + getNodeId());
				refresh();
			}
		}, this.config.restoreInterval(), this.config.restoreInterval(), TimeUnit.MILLISECONDS);
         
         this.persistanceScheduleTask = eventLoop.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				//Save Routing Table To File
				routingTable.save();				
			}
		}, this.config.persistanceInterval(), this.config.persistanceInterval(), TimeUnit.MILLISECONDS);
    }
    
    public void refresh() {
        try
        {
            new RefreshOperation(getNode()).execute();
        }
        catch (Throwable e)
        {
            logger.error("Refresh Operation Failed; Message: " + e.getMessage());
            e.printStackTrace();
        }
		
    }

    public final void stopRefreshOperation()
    {
    	if(this.refreshScheduleTask!=null) {
	        this.refreshScheduleTask.cancel(true);
	        this.refreshScheduleTask = null;
    	}
    }

    public MDHT getDHT()
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
    
    public void putContentLocally(MContent content) throws Throwable
    {
    	this.dht.put(content.getMeta());
    	this.dht.store(content);
    }

    public List<MId> putContent(MContent content) throws Throwable
    {
        StoreOperation sop = new StoreOperation(this, content);
        sop.execute();

        /* Return how many nodes the content was stored on */
        return sop.getNodesStoredAt();
    }

    public MContent get(MContentKey key) throws Throwable
    {
        if (this.dht.contains(key))
        {
            /* If the content exist in our own DHT, then return it. */
            return this.dht.getContent(key);
        }

        System.nanoTime();
        List<MId> nodesFound = lookupContent(key, 1);
        if(nodesFound.size()==0)
        	return null;
        
        /* Retrieve Content From Other Node */
        retrive(nodesFound.get(0), key);
        System.nanoTime();

        /* Load From Local DHT */
        return this.dht.getContent(key);
    }
    
    public boolean contains(MContentKey key) {
    	return this.dht.contains(key);
    }
    
    /**
     * 
     * Lookup content on Nodes exclude local node
     * 
     * @param param
     * @param maxNodes
     * @return
     * @throws Throwable
     */
    public List<MId> lookupContent(MContentKey key, int maxNodes) throws Throwable{
        ContentLookupOperation clo = new ContentLookupOperation(this, key, maxNodes);
        clo.execute();
        return clo.getNodesFound();
    }
    
    public void retrive(MId remoteNode, MContentKey key) throws Throwable {
        ContentRetrieveOperation cro = new ContentRetrieveOperation(this, key, remoteNode);
        cro.execute();
    }

    public NetService getNetService() {
    	return netService;
    }

    public void shutdown() throws IOException
    {
        /* Shut down the server */
    	netService.shutdown();
    	routingTable.save();

        this.stopRefreshOperation();
    }

    public IRoutingTable getRoutingTable()
    {
        return this.routingTable;
    }

    public Statistician getStatistician()
    {
        return this.statistician;
    }
    
    public void clearCache(int maxContentBytes) {
    	
    }

    /**
     * Creates a string containing all data about this Kademlia instance
     *
     * @return The string representation of this Kad instance
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\n\nPrinting Kad State for instance with owner : \n");
        
        sb.append("Local Node : ");
        sb.append(this.getNodeId());
        // sb.append("\n");

        // sb.append("\n");
        // sb.append("Routing Table: ");
        // sb.append(this.getRoutingTable());
        // sb.append("\n");

        // sb.append("\n");
        sb.append("\nDHT: ");
        sb.append(this.dht);
        sb.append("\n\n");

        return sb.toString();
    }
    
    public void setName(String name) {
    	netService.getPeerInfo().setDeviceInfo(name);
    }
    
    public String getName() {
    	return netService.getPeerInfo().getDeviceInfo();
    }

	public MId getNodeId() {
		return netService.getMId();
	}

	public MId getParentId() {
		return parentId;
	}

	public void setParentId(MId parentId) {
		this.parentId = parentId;
	}

	@Override
	public NioEventLoopGroup getEventLoop() {
		return eventLoop;
	}

	public void setEventLoop(NioEventLoopGroup eventLoop) {
		this.eventLoop = eventLoop;
	}
	
	public MNode getNode() {
		return this;
	}
	
	@Override
	public void onPeerLinkOpen(NetService service, PeerLink link) {
		routingTable.insert(link.getRemoteMId());
		System.out.println("Routing Table insert Link " + link.getRemoteMId());
	}

	@Override
	public void onPeerLinkClose(NetService service, PeerLink link) {
		routingTable.setUnresponsiveContact(link.getRemoteMId());
		System.out.println("Routing Table Remove Link " + link.getRemoteMId());
	}
	
}
