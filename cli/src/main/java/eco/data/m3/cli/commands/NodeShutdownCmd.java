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
import eco.data.m3.routing.api.rest.request.SaveNodeStateService;
import eco.data.m3.routing.api.rest.request.ShutdownNodeService;
import eco.data.m3.routing.api.rest.response.SaveNodeStateResponse;
import eco.data.m3.routing.api.rest.response.ShutdownNodeResponse;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
* @author: xquan
* Shutdown a specified Node Command.
* @since: Created in 2018-6-30
**/
@Command(name = "nshutdown", description="Shutdown node.")
public class NodeShutdownCmd implements Runnable {

	@Parameters(paramLabel = "node_name", description = "Node's name.")
	private String nodeName ;
	
	@Override
	public void run() {
		RestClient client = new RestClient();
		ShutdownNodeService req = new ShutdownNodeService();
		req.setNodeName(nodeName);
		ShutdownNodeResponse resp = (ShutdownNodeResponse) client.doRequest(req);
		if(resp!=null)		
			System.out.println(resp.Code);
	}

}
