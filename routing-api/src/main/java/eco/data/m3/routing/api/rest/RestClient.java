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

package eco.data.m3.routing.api.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
* @Author: xquan
* @Description: A Rest Client for communicate Node Server.
* @Date: Created in 2018-6-29
**/
public class RestClient {
	
	private String host_addr = Common.KAD_HOST;
	
	public RestClient()
	{		
	}
	
	public RestClient(String host)
	{
		host_addr = host;
	}

	private BaseResponse buildResponse(HttpURLConnection conn, String serviceName)
			throws IOException, JsonSyntaxException, ClassNotFoundException {
		BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

		String output = responseBuffer.toString();
		StringBuffer sb = new StringBuffer();
		while ((output = responseBuffer.readLine()) != null) {
			sb.append(output);
		}
//		System.out.println(sb.toString());
		Gson gson = new Gson();
		String className = "eco.data.m3.routing.api.rest.response." + serviceName.replace("Service", "Response");
		return (BaseResponse) gson.fromJson(sb.toString(),
				Class.forName(className));
	}

	public BaseResponse doRequest(BaseRequest req) {
		try {
			String serviceName = req.getServiceName();
			String input = req.toJSON();
			URL targetUrl = new URL(req.getServiceAddr(host_addr));

			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "application/json");

			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(input.getBytes());
			outputStream.flush();

			if (httpConnection.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : " + httpConnection.getResponseCode());
				System.err.println("Failed : HTTP error code : " + httpConnection.getResponseCode());
//				return;
			}

			BaseResponse resp = buildResponse(httpConnection, serviceName);
			httpConnection.disconnect();
			return resp;

		} catch (MalformedURLException e) {
			System.err.println("MalformedURL : "+ req.getServiceAddr(host_addr));
		} catch (IOException e) {
			System.err.println("Cannot connect to Server.");
		} catch (JsonSyntaxException e) {
			System.err.println("Return Content Format Error");
		} catch (ClassNotFoundException e) {
			System.err.println("No Response Handler: " + req.getServiceName());
		}
		return null;
	}
}
