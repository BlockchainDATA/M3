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

import eco.data.m3.routing.api.core.ContentInfo;
import eco.data.m3.routing.api.core.ErrorCode;
import eco.data.m3.routing.api.domain.KadService;
import eco.data.m3.routing.api.utils.CHexConvert;
import eco.data.m3.web.service.BaseServlet;
import eco.data.m3.web.service.RequestResult;

/**
 * @author: xquan
 * Get Content Service.
 * Get Saved Content from DHT Network.
 * @since: 2018-7-3
 **/
public class GetContentService extends BaseServlet{

	class RequestResultGetNodeInfo extends RequestResult
	{
		ContentInfo content ;
	}
	
	@Override
	public RequestResult handleRequest(HashMap<String, Object> req) {
		RequestResultGetNodeInfo rt = new RequestResultGetNodeInfo();

		String nodeName =  (String) req.get("nodeName");		
		String key =  (String) req.get("key");		
		String ownerId =  (String) req.get("ownerId");				
		
		if(nodeName==null || key==null || ownerId==null)
		{
			rt.Code = ErrorCode.ParameterError;
			return rt;
		}
		
		byte [] keyb = CHexConvert.hexStr2Bytes(key);
		if(keyb.length!=20)
		{
			rt.Code = ErrorCode.ParameterError;
			return rt;			
		}
		
		rt.content = KadService.getInstance().getContent(nodeName, keyb, ownerId);
		if(rt.content==null)
			rt.Code = ErrorCode.NodeNotFound;
		else
			rt.Code = ErrorCode.Success;
		return rt;
	}

}
