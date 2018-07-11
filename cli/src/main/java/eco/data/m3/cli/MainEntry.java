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

package eco.data.m3.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import eco.data.m3.cli.commands.GetCmd;
import eco.data.m3.cli.commands.Greet;
import eco.data.m3.cli.commands.HelpCmd;
import eco.data.m3.cli.commands.HostCmd;
import eco.data.m3.cli.commands.NodeConnectCmd;
import eco.data.m3.cli.commands.NodeConnectRemoteCmd;
import eco.data.m3.cli.commands.NodeCreateCmd;
import eco.data.m3.cli.commands.NodeInfoCmd;
import eco.data.m3.cli.commands.NodeListCmd;
import eco.data.m3.cli.commands.NodeRefreshCmd;
import eco.data.m3.cli.commands.NodeRouteCmd;
import eco.data.m3.cli.commands.NodeSaveCmd;
import eco.data.m3.cli.commands.NodeShutdownAllCmd;
import eco.data.m3.cli.commands.NodeShutdownCmd;
import eco.data.m3.cli.commands.ServerListCmd;
import eco.data.m3.cli.commands.StoreCmd;

import picocli.CommandLine;

/**
* @author: xquan
* Main Entry. 
* Command line's main entry.
* Use picocli library to parse command line.
* https://github.com/remkop/picocli
* @since: Created in 2018-6-30
**/
public class MainEntry  {
	
	public static void main(String[] args) {
		Scanner cin = new Scanner(System.in);
		boolean running = true;
		
		System.out.println("======== KADCLI Project =======");
        CommandLine.run(new HelpCmd(), args);

		while(running)
		{
			String line = cin.nextLine();
			List<String> line_args = Arrays.asList(line.split(" "));
			String cmd = line_args.get(0);
			String[] cmd_args = {};
			
			if(line_args.size()>1)
			{
				List<String> addon_args = line_args.subList(1, line_args.size());
				cmd_args = (String[]) addon_args.toArray(new String[0]);
			}
//			String[] cmd_args = (String[]) line_args.toArray();			
			
			switch (cmd) {
			case "quit":
				running = false;
				break;
			case "":
				break;
			case "host":
		        CommandLine.run(new HostCmd(), cmd_args);
				break;
			case "ncreate":
		        CommandLine.run(new NodeCreateCmd(), cmd_args);
				break;
			case "nconnect":
		        CommandLine.run(new NodeConnectCmd(), cmd_args);
				break;
			case "nconnectr":
		        CommandLine.run(new NodeConnectRemoteCmd(), cmd_args);
				break;
			case "ninfo":
		        CommandLine.run(new NodeInfoCmd(), cmd_args);
				break;
			case "nlist":
		        CommandLine.run(new NodeListCmd(), cmd_args);
				break;
			case "nrefresh":
		        CommandLine.run(new NodeRefreshCmd(), cmd_args);
				break;
			case "nroute":
		        CommandLine.run(new NodeRouteCmd(), cmd_args);
				break;
			case "nsave":
		        CommandLine.run(new NodeSaveCmd(), cmd_args);
				break;
			case "nshutdown":
		        CommandLine.run(new NodeShutdownCmd(), cmd_args);
				break;
			case "nshutall":
		        CommandLine.run(new NodeShutdownAllCmd(), cmd_args);
				break;
			case "get":
		        CommandLine.run(new GetCmd(), cmd_args);
				break;
			case "store":
		        CommandLine.run(new StoreCmd(), cmd_args);
				break;
			case "greet":
		        CommandLine.run(new Greet(), cmd_args);
		        break;
			case "slist":
		        CommandLine.run(new ServerListCmd(), cmd_args);
				break;
			case "help":
		        CommandLine.run(new HelpCmd(), cmd_args);
		        break;
			default:
		        CommandLine.run(new HelpCmd(), cmd_args);
				break;
			}
		}			
		
		cin.close();
	}
	
}
