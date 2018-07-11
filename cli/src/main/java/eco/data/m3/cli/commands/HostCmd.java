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

import eco.data.m3.routing.api.rest.Common;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
* @author: xquan
* Host Command, Show the Host which is current controlled.
* @since: 2018-6-30
**/
@Command(name = "host", description="Change Current Host Address.")
public class HostCmd implements Runnable {

//	@Parameters(paramLabel = "host_addr", description = "Host Inet Address, 127.0.0.1 default.")
	@Option(names = {"-a", "--host_addr"}, required = false, description = "Host Inet Address, 127.0.0.1 default.")
    private String host_addr;

	@Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
	boolean usageHelpRequested;

	@Override
	public void run() {
		if(usageHelpRequested)
		{
			CommandLine.usage(this, System.out);
			return;
		}
		if(host_addr!=null && host_addr.length()>0)
			Common.KAD_HOST = host_addr;		
		System.out.println("Current Host:" + Common.KAD_HOST);
	}

}
