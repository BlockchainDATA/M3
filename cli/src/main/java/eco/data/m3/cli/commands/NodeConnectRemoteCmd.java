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

package eco.data.m3.cli.commands;

import eco.data.m3.routing.api.rest.RestClient;
import eco.data.m3.routing.api.rest.request.ConnectRemoteNodeService;
import eco.data.m3.routing.api.rest.response.ConnectRemoteNodeResponse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
* @Author: xquan
* @Description: Remote Connect Command. Joint node to remote parent node.
* @Date: Created in 2018-6-30
**/
@Command(name = "nconnectr", description="Connect node to remote parent node.")
public class NodeConnectRemoteCmd implements Runnable {

	@Option(names = {"-n", "--nodeName"}, required = true, description = "Node's name (unique in this host).")
	private String nodeName ;

	@Option(names = {"-p", "--rport"}, required = true, description = "Node's Parent Port.")
	private int rport;

	@Option(names = {"-a", "--rinetaddr"}, required = true, description = "Node's Parent Inet Address.")
	private String rinetaddr;

	@Option(names = {"-k", "--rkid"}, required = true, description = "Node's Parent id.")
	private String rkid;

	@Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
	boolean usageHelpRequested;
	
	@Override
	public void run() {
		if(usageHelpRequested)
		{
			CommandLine.usage(this, System.out);
			return;
		}
		
		RestClient client = new RestClient();

		ConnectRemoteNodeService cReq = new ConnectRemoteNodeService();
		cReq.setNodeName(nodeName);
		cReq.setKid(rkid);
		cReq.setInetaddr(rinetaddr);
		cReq.setPort(rport);
		ConnectRemoteNodeResponse resp = (ConnectRemoteNodeResponse) client.doRequest(cReq);
		if(resp!=null)		
			System.out.println(resp.Code);
		
	}

}
