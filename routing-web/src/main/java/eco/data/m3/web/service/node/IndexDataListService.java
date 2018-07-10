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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eco.data.m3.routing.api.core.ErrorCode;
import eco.data.m3.web.service.BaseServlet;
import eco.data.m3.web.service.RequestResult;

/**
 * @Author: xquan
 * @Description: List Index Data Service.
 * List Index Data Saved in index Server.
 * @Date: Created in 2018-7-3
 **/
public class IndexDataListService extends BaseServlet{

	class RequestResultIndexDataList extends RequestResult
	{
		List<String> files = new ArrayList();
	}
	
	@Override
	public RequestResult handleRequest(HashMap<String, Object> req) {
		RequestResultIndexDataList rt = new RequestResultIndexDataList();

		String path = getServletContext().getRealPath("")+"/kad_index_data/";
		File root = new File(path);
		for(File f : root.listFiles())
		{
			rt.files.add(f.getName());
		}
		
		rt.Code = ErrorCode.Success;
		return rt;
	}

}
