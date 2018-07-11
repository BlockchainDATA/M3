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

 package eco.data.m3.routing.api.rest.request;

import eco.data.m3.routing.api.rest.BaseRequest;

/**
* @author: xquan
* Rest Request
	create a node to in a host.
* @since: 2018-6-29
**/
public class CreateNodeService extends BaseRequest{

	private String name ;
	
	private String kid ;	
	
	private int port;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKid() {
		return kid;
	}
	
	public void setKid(String kid) {
		this.kid = kid;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}	
}
