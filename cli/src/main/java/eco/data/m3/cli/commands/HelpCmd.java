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


import java.lang.annotation.Annotation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
* @Author: xquan
* @Description: Help Command.
* @Date: Created in 2018-6-30
**/
@Command(name = "help", header = "%n@|green Hello world demo|@", description="Show Help Content.")
public class HelpCmd implements Runnable {

	private void printCmdInfo(Class cmd)
	{
		Annotation[] ans = cmd.getAnnotations();
		if(ans.length>0)
		{
			Command an = (Command) ans[0];
			System.out.print(String.format("%-15s", an.name()));

			for(String str : an.description())
				System.out.print(str);
			
			System.out.println();
		}
	}

    public void run() {
        System.out.println("Command List ( 'quit' to exit ): ");

        printCmdInfo(HostCmd.class);
        printCmdInfo(ServerListCmd.class);
        printCmdInfo(NodeCreateCmd.class);
        printCmdInfo(NodeConnectCmd.class);
        printCmdInfo(NodeConnectRemoteCmd.class);
        
        printCmdInfo(NodeInfoCmd.class);
        printCmdInfo(NodeListCmd.class);
        printCmdInfo(NodeRefreshCmd.class);
        printCmdInfo(NodeRouteCmd.class);
        printCmdInfo(NodeSaveCmd.class);

        printCmdInfo(NodeShutdownCmd.class);
        printCmdInfo(NodeShutdownAllCmd.class);

        printCmdInfo(StoreCmd.class);
        printCmdInfo(GetCmd.class);
        printCmdInfo(HelpCmd.class);
    }

    public static void main(String... args) {
        CommandLine.run(new HelpCmd(), args);
    }
}