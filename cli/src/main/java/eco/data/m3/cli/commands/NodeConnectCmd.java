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
import eco.data.m3.routing.api.rest.request.ConnectNodeService;
import eco.data.m3.routing.api.rest.response.ConnectNodeResponse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
* @author: xquan
* Connect Command. Joint node to local parent node.
* @since: 2018-6-30
**/
@Command(name = "nconnect", description="Joint node to local parent node.")
public class NodeConnectCmd implements Runnable {

	@Option(names = {"-n", "--nodeName"}, required = true, description = "Node's name (unique in this host).")
//	@Parameters(paramLabel = "nodeName", description = "Node's name (unique in this host).")
	private String nodeName ;

	@Option(names = {"-p", "--parentName"}, required = true, description = "Node's Parent Name.")
//	@Parameters(paramLabel = "parentName", description = "Node's Parent Name.")
	private String parentName;

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

		ConnectNodeService cReq = new ConnectNodeService();
		cReq.setNodeName(nodeName);
		cReq.setParentName(parentName);
		ConnectNodeResponse resp = (ConnectNodeResponse) client.doRequest(cReq);
		if(resp!=null)		
			System.out.println(resp.Code);
		
	}

}
