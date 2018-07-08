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
import eco.data.m3.routing.api.rest.request.CreateNodeService;
import eco.data.m3.routing.api.rest.response.CreateNodeResponse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
* @Author: xquan
* @Description: Create Node Command. Create Kad Node on current host.
* @Date: Created in 2018-6-30
**/
@Command(name = "ncreate", description="Create Kad Node on current host.")
public class NodeCreateCmd implements Runnable {

	@Option(names = {"-n", "--name"}, required = true, description = "Node's name (unique in this host).")
	private String name ;

	@Option(names = {"-k", "--kid"}, required = false, description = "Node's id (must be 20 bytes, eg. 01234567890123456789)")
	private String kid ;	

	@Option(names = {"-p", "--port"}, required = true, description = "Node's UDP Port (eg. 9000)")
	private int port = 9000;

	@Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
	boolean usageHelpRequested;
	
	@Override
	public void run() {
		if(usageHelpRequested)
		{
			CommandLine.usage(this, System.out);
			return;
		}
		
		CreateNodeService request = new CreateNodeService(); 
		request.setName(name);
		request.setPort(port);
		request.setKid(kid);
		
		RestClient client = new RestClient();
		CreateNodeResponse resp = (CreateNodeResponse) client.doRequest(request);
		if(resp!=null)		
			System.out.println(resp.Code);
	}

}
