package eco.data.m3.routing.core;

public class NodeSetting {

	private String dhtType;
	
	private String name;
	
	private String nodeId;
	
	private String parentId;

	private String networkType;
	
	private int port;
	
	private boolean isLoadFromFile;
	
	private int latency = -1;
	
	public String getDhtType() {
		return dhtType;
	}

	public void setDhtType(String dhtType) {
		this.dhtType = dhtType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isLoadFromFile() {
		return isLoadFromFile;
	}

	public void setLoadFromFile(boolean isLoadFromFile) {
		this.isLoadFromFile = isLoadFromFile;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
}
