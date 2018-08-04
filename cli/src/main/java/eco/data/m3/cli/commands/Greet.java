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


import picocli.CommandLine;
import static picocli.CommandLine.*;

/**
* @author: xquan
* A Command Example.
* @since: 2018-6-30
**/
@Command(name = "Greet", header = "%n@|green Hello world demo|@", description="Show Greet Info")
public class Greet implements Runnable {

    @Option(names = {"-u", "--user"}, required = true, description = "The user name.")
    String userName;

    public void run()
    {
        System.out.println("Hello, " + userName);
    }

    public static void main(String... args) {
        CommandLine.run(new Greet(), args);
    }
}