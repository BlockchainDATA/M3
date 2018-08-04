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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import eco.data.m3.routing.api.core.ErrorCode;
import eco.data.m3.web.service.BaseServlet;
import eco.data.m3.web.service.RequestResult;

/**
 * @author: xquan
 * Get Index Data Service.
 * Get an Index File Data.
 * @since: 2018-7-3
 **/
public class IndexDataGetService extends BaseServlet{

	class RequestResultIndexDataGet extends RequestResult
	{
		String data ;
	}
	
	@Override
	public RequestResult handleRequest(HashMap<String, Object> req) {
		RequestResultIndexDataGet rt = new RequestResultIndexDataGet();

		String name =  (String) req.get("name");	
		if(name==null)
		{
			rt.Code = ErrorCode.ParameterError;
			return rt;
		}

		String path = getServletContext().getRealPath("")+"/kad_index_data/"+name;
		try {
	        final File inputFile = new File(path);
	        final int fileSize = (int) inputFile.length();
			final byte [] allBytes = new byte[fileSize];
			FileReader reader = new FileReader(path);
	        InputStream in = new FileInputStream(inputFile);
	        int bytesRead = in.read(allBytes, 0, fileSize);
	        rt.data = new String(allBytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rt.Code = ErrorCode.ContentNotFound;
			return rt;
		} catch (IOException e) {
			e.printStackTrace();
			rt.Code = ErrorCode.ServiceError;
			return rt;
		}
		rt.Code = ErrorCode.Success;
		return rt;
	}

}
