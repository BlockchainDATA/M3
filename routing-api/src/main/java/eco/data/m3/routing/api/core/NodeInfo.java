/*
 * Copyright (C) 2018 Blockchain Data Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eco.data.m3.routing.api.core;

import java.io.Serializable;

/**
 * @author: xquan
 * basic Node Information of a Kad Node.
 * @since: 2018-6-29
 **/
public class NodeInfo implements Serializable{
	
	private String name;
	
	private int port;
	
	private String nodeId;	
	
	private String parentIp;
	
	private int parentPort;
	
	private String parentNodeId;
	
	//Statistician
	
	private long bootStrapTime;
	
	private long totalDataReceived;
	
	private long totalDataSent;
	
	private double averageContentLookupRouteLength;
	
	private double averageContentLookupTime;
	
	private int numContentLookups;
	
	private int numFailedContentLookups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public long getBootStrapTime() {
		return bootStrapTime;
	}

	public void setBootStrapTime(long bootStrapTime) {
		this.bootStrapTime = bootStrapTime;
	}

	public long getTotalDataReceived() {
		return totalDataReceived;
	}

	public void setTotalDataReceived(long totalDataReceived) {
		this.totalDataReceived = totalDataReceived;
	}

	public long getTotalDataSent() {
		return totalDataSent;
	}

	public void setTotalDataSent(long totalDataSent) {
		this.totalDataSent = totalDataSent;
	}

	public double getAverageContentLookupRouteLength() {
		return averageContentLookupRouteLength;
	}

	public void setAverageContentLookupRouteLength(double averageContentLookupRouteLength) {
		this.averageContentLookupRouteLength = averageContentLookupRouteLength;
	}

	public double getAverageContentLookupTime() {
		return averageContentLookupTime;
	}

	public void setAverageContentLookupTime(double averageContentLookupTime) {
		this.averageContentLookupTime = averageContentLookupTime;
	}

	public int getNumContentLookups() {
		return numContentLookups;
	}

	public void setNumContentLookups(int numContentLookups) {
		this.numContentLookups = numContentLookups;
	}

	public int getNumFailedContentLookups() {
		return numFailedContentLookups;
	}

	public void setNumFailedContentLookups(int numFailedContentLookups) {
		this.numFailedContentLookups = numFailedContentLookups;
	}

	@Override
	public String toString() {
		
		return super.toString();
	}
	
	public String getParentIp() {
		return parentIp;
	}

	public void setParentIp(String parentIp) {
		this.parentIp = parentIp;
	}

	public int getParentPort() {
		return parentPort;
	}

	public void setParentPort(int parentPort) {
		this.parentPort = parentPort;
	}

	public String getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(String parentNodeId) {
		this.parentNodeId = parentNodeId;
	}
}
