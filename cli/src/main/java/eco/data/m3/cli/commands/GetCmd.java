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

import eco.data.m3.cli.utils.PropertyPrinter;
import eco.data.m3.routing.api.rest.RestClient;
import eco.data.m3.routing.api.rest.request.GetContentService;
import eco.data.m3.routing.api.rest.response.GetContentResponse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
* @author: xquan
* Get Saved Data's Content Command.
* @since: 2018-6-30
**/
@Command(name = "get", description="Get Content.")
public class GetCmd implements Runnable {

	@Option(names = {"-n", "--node_name"}, required = true, description = "Node's name.")
	private String nodeName ;

	@Option(names = {"-k", "--key"}, required = true, description = "The content's key.")
	private String key ;
	
	@Option(names = {"-o", "--owner"}, required = true, description = "The owner's id.")
	private String ownerId ;

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
		GetContentService req = new GetContentService();
		req.setNodeName(nodeName);
		req.setKey(key);
		req.setOwnerId(ownerId);
		GetContentResponse resp = (GetContentResponse) client.doRequest(req);
		if(resp!=null)		
			System.out.println(PropertyPrinter.print(resp.getContent()));
	}

}