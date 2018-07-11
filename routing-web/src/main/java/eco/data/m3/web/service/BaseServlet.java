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

package eco.data.m3.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import eco.data.m3.routing.api.core.ErrorCode;

/**
 * @author: xquan
 * Base Servlet Template.
 * Supply Basic Servlet functions.
 * Response will formated to GSon String.
 * @since: Created in 2018-7-3
 **/
public abstract class BaseServlet extends HttpServlet{
 
	private static final long serialVersionUID = 8687515640891744694L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		
		HashMap<String, Object> jsonReq = getJsonObject(req);
		RequestResult rt;
		if(!checkRequest(jsonReq)){
			rt = new RequestResult();
			rt.Code = ErrorCode.ParameterError;
		}else{
			rt = handleRequest(jsonReq);
		}

		returnResult(rt, resp);
	}
	
	public abstract RequestResult handleRequest(HashMap<String, Object> jsonReq);
	
	public boolean checkRequest(HashMap<String, Object> req){	
		return true;
	}
	
	public void returnResult(RequestResult rt, HttpServletResponse resp) throws IOException
	{
		String result = "" ;
		
		rt.Result = rt.Code.ordinal();
		Gson gson = new Gson();
		result = gson.toJson(rt);		
		
		resp.setCharacterEncoding("utf-8");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setContentType("text/javascript");
		PrintWriter writer = resp.getWriter();
		
		writer.print(result);
		writer.flush();
		writer.close();		
	}	
	
	public HashMap<String, Object> getJsonObject(HttpServletRequest request) throws IOException{
		String resultStr = "";
        String readLine;
        StringBuffer sb = new StringBuffer();
        BufferedReader responseReader = null;
        OutputStream outputStream = null;
        try {
            responseReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            resultStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
            outputStream.close();
            }
        } 

		Gson gson = new Gson();
		HashMap<String, Object> result = gson.fromJson(resultStr, HashMap.class);
		if(result == null)
			result = new HashMap<>();
		return result;
	} 
}
