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
import eco.data.m3.routing.api.core.StorageInfo;
import eco.data.m3.routing.api.domain.KadService;
import eco.data.m3.web.service.BaseServlet;
import eco.data.m3.web.service.RequestResult;

/**
 * @Author: xquan
 * @Description: Get Storage Info Service.
 * Get a Storage Info saved in DHT Network.
 * @Date: Created in 2018-7-3
 **/
public class GetStorageInfoService extends BaseServlet{

	class RequestResultGetStorageInfo extends RequestResult
	{
		StorageInfo storage ;
	}
	
	@Override
	public RequestResult handleRequest(HashMap<String, Object> req) {
		RequestResultGetStorageInfo rt = new RequestResultGetStorageInfo();

		String nodeName =  (String) req.get("nodeName");				
		
		if(nodeName==null)
		{
			rt.Code = ErrorCode.ParameterError;
			return rt;
		}
		
		rt.storage = KadService.getInstance().getStorageInfo(nodeName);
		if(rt.storage==null)
			rt.Code = ErrorCode.NodeNotFound;
		else
			rt.Code = ErrorCode.Success;
		return rt;
	}

}
