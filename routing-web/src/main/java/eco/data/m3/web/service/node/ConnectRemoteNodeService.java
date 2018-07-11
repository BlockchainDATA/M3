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

package eco.data.m3.web.service.node;

import java.util.HashMap;

import eco.data.m3.routing.api.core.ErrorCode;
import eco.data.m3.routing.api.domain.KadService;
import eco.data.m3.web.service.BaseServlet;
import eco.data.m3.web.service.RequestResult;

/**
 * @author: xquan
 * Connect Remote Node Service.
 * Let a Node Joint to a specified remote Node.
 * @since: 2018-7-3
 **/
public class ConnectRemoteNodeService extends BaseServlet{
	
	@Override
	public RequestResult handleRequest(HashMap<String, Object> req) {
		RequestResult rt = new RequestResult();

		String nodeName =  (String) req.get("nodeName");		
		Double port =  (Double) req.get("port");		
		String inetaddr =  (String) req.get("inetaddr");		
		String kid =  (String) req.get("kid");	
		
		if(nodeName==null || port==null || inetaddr==null || kid==null)
		{
			rt.Code = ErrorCode.ParameterError;
			return rt;
		}
		
		rt.Code = KadService.getInstance().connectNode(nodeName, kid, inetaddr, port.intValue());		
		return rt;
	}

}
