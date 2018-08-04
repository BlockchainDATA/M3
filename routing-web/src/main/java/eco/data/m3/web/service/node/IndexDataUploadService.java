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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import eco.data.m3.routing.api.core.ErrorCode;
import eco.data.m3.web.service.BaseServlet;
import eco.data.m3.web.service.RequestResult;

/**
 * @author: xquan
 * Upload Index Data Service.
 * Upload Index Data to index server.
 * @since: 2018-7-3
 **/
public class IndexDataUploadService extends BaseServlet{
	
	@Override
	public RequestResult handleRequest(HashMap<String, Object> req) {

		RequestResult rt = new RequestResult();
		
		String data =  (String) req.get("data");		

		if(data==null)
		{
			rt.Code = ErrorCode.ParameterError;
			return rt;
		}

		try {
			String name = System.currentTimeMillis() +".json";
			String path = getServletContext().getRealPath("")+"/kad_index_data/" + name;
						
            FileWriter writer = new FileWriter(path);
            writer.write(data);
            writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rt.Code = ErrorCode.Success;
		return rt;
	}

}
