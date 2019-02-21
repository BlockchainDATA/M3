package eco.data.m3.routing.operation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eco.data.m3.content.MContent;
import eco.data.m3.content.MContentKey;
import eco.data.m3.content.MContentMeta;
import eco.data.m3.net.core.MId;
import eco.data.m3.routing.MNode;

public class ContentRefreshOperation implements IOperation {

	private static final Logger logger = LoggerFactory.getLogger(ContentRefreshOperation.class.getName());

	private final MNode localNode;

	public ContentRefreshOperation(MNode localNode) {
		this.localNode = localNode;
	}

	/**
	 * For each content stored on this DHT, distribute it to the K closest nodes
	 * Also delete the content if this node is no longer one of the K closest nodes
	 * 
	 * We assume that our JKademliaRoutingTable is updated, and we can get the K
	 * closest nodes from that table
	 * 
	 * @throws Throwable
	 */
	@Override
	public void execute() throws Throwable {
		/* Get a list of all storage entries for content */
		List<MContentMeta> entries = localNode.getDHT().getContents();

		/*
		 * If a content was last republished before this time, then we need to republish
		 * it
		 */
		final long minRepublishTime = (System.currentTimeMillis())
				- localNode.getCurrentConfiguration().restoreInterval();

		/* For each storage entry, distribute it */
		for (MContentMeta e : entries) {
			/*
			 * Check last update time of this entry and only distribute it if it has been
			 * last updated > 1 hour ago
			 */
			if (e.getLastRepublished() > minRepublishTime) {
				continue;
			}

			/* Set that this content is now republished */
			e.setLastRepublished(System.currentTimeMillis());
			this.localNode.getDHT().put(e);

			/* Get the K closest nodes to this entries */
			List<MId> closestNodes = this.localNode.getRoutingTable().findClosest(e.getKey(),
					localNode.getCurrentConfiguration().k());
			
			try {
				MContent content = localNode.getDHT().getContent(new MContentKey(e));
				
				StoreToNodesOperation sop = new StoreToNodesOperation(localNode, content, closestNodes);
				sop.execute();
			}catch (Exception e1) {
				e1.printStackTrace();
			}

			/*
			 * Delete any content on this node that this node is not one of the K-Closest
			 * nodes to
			 */ 
			if (!closestNodes.contains(this.localNode.getNodeId())) {
				localNode.getDHT().remove(e);
			}
		}
	}

}
