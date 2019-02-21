package eco.data.m3.routing.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eco.data.m3.net.core.KeyComparator;
import eco.data.m3.net.core.MId;

public class LookupQueue {

	private Map<MId, LookupStatus> nodeMap = new HashMap<>();

	/* Used to sort nodes */
	private final Comparator<MId> comparator;

	private int k;

	public LookupQueue(MId lookupId, int k) {
		this.comparator = new KeyComparator(lookupId);
		this.nodeMap = new TreeMap<MId, LookupStatus>(this.comparator);
		this.k = k;
	}

	public void put(MId nodeId, LookupStatus status) {
		synchronized (this) {
			nodeMap.put(nodeId, status);
		}
	}

	public void addNodes(List<MId> list) {
		synchronized (this) {
			for (MId o : list) {
				/* If this node is not in the list, add the node */
				if (!nodeMap.containsKey(o)) {
					nodeMap.put(o, LookupStatus.UnAsked);
				}
			}
		}
	}

	/**
	 * @param status The status of the nodes to return
	 *
	 * @return The K closest nodes to the target lookupId given that have the
	 *         specified status
	 */
	public List<MId> closestNodes(LookupStatus status) {
		List<MId> closestNodes = new ArrayList<>(k);
		int remainingSpaces = k;

		synchronized (this) {
			for (Map.Entry<MId, LookupStatus> e : nodeMap.entrySet()) {
				if (status.equals(e.getValue())) {
					/* We got one with the required status, now add it */
					closestNodes.add((MId) e.getKey());
					if (--remainingSpaces == 0) {
						break;
					}
				}
			}
		}

		return closestNodes;
	}

	/**
	 * Find The K closest nodes to the target lookupId given that have not FAILED.
	 * From those K, get those that have the specified status
	 *
	 * @param status The status of the nodes to return
	 *
	 * @return A List of the closest nodes
	 */
	public List<MId> closestNodesNotFailed(LookupStatus status) {
		List<MId> closestNodes = new ArrayList<>(k);
		int remainingSpaces = k;

		synchronized (this) {
			for (Map.Entry<MId, LookupStatus> e : nodeMap.entrySet()) {
				if (!LookupStatus.Failed.equals(e.getValue())) {
					if (status.equals(e.getValue())) {
						/* We got one with the required status, now add it */
						closestNodes.add(e.getKey());
					}

					if (--remainingSpaces == 0) {
						break;
					}
				}
			}
		}

		/* Sort nodes according to criteria */
		Collections.sort(closestNodes, this.comparator);

		return closestNodes;
	}

	public List<MId> getAwatingNodes() {
		return getNodesOfState(LookupStatus.Awating);
	}

	public List<MId> getFailedNodes() {
		return getNodesOfState(LookupStatus.Failed);
	}

	public List<MId> getNodesOfState(LookupStatus status) {
		List<MId> failedNodes = new ArrayList<>();

		synchronized (this) {
			for (Map.Entry<MId, LookupStatus> e : nodeMap.entrySet()) {
				if (e.getValue().equals(status)) {
					failedNodes.add(e.getKey());
				}
			}
		}

		return failedNodes;
	}
}
