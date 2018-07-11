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

package eco.data.m3.cli.rest;

import eco.data.m3.routing.api.core.ErrorCode;
import eco.data.m3.routing.api.rest.RestClient;
import eco.data.m3.routing.api.rest.request.ConnectNodeService;
import eco.data.m3.routing.api.rest.request.CreateNodeService;
import eco.data.m3.routing.api.rest.request.GetContentService;
import eco.data.m3.routing.api.rest.request.GetNodeInfoService;
import eco.data.m3.routing.api.rest.request.GetNodeRouteService;
import eco.data.m3.routing.api.rest.request.GetStorageInfoService;
import eco.data.m3.routing.api.rest.request.ListNodesService;
import eco.data.m3.routing.api.rest.request.ShutdownAllNodesService;
import eco.data.m3.routing.api.rest.request.ShutdownNodeService;
import eco.data.m3.routing.api.rest.request.StoreContentService;
import eco.data.m3.routing.api.rest.response.ConnectNodeResponse;
import eco.data.m3.routing.api.rest.response.CreateNodeResponse;
import eco.data.m3.routing.api.rest.response.GetContentResponse;
import eco.data.m3.routing.api.rest.response.GetNodeInfoResponse;
import eco.data.m3.routing.api.rest.response.GetNodeRouteResponse;
import eco.data.m3.routing.api.rest.response.GetStorageInfoResponse;
import eco.data.m3.routing.api.rest.response.ListNodesResponse;
import eco.data.m3.routing.api.rest.response.ShutdownAllNodesResponse;
import eco.data.m3.routing.api.rest.response.ShutdownNodeResponse;
import eco.data.m3.routing.api.rest.response.StoreContentResponse;

import junit.framework.TestCase;
import org.junit.Test;

/**
* @author: xquan
* Main Entry. 
* Command line's main entry.
* Use picocli library to parse command line.
* https://github.com/remkop/picocli
* @since: Created in 2018-6-30
**/

public class RestClientTest extends TestCase {
	

	@Test
	public void testCreateNodeService() {
		CreateNodeService request = new CreateNodeService(); 
		request.setName("First");
		request.setPort(7070);
		
		RestClient client = new RestClient();
		CreateNodeResponse resp = (CreateNodeResponse) client.doRequest(request);
		assertEquals(ErrorCode.Success, resp.Code);
		
		resp = (CreateNodeResponse) client.doRequest(request);
		assertEquals(ErrorCode.NameAlreadyExsit, resp.Code);
		
		ShutdownNodeService shutReq = new ShutdownNodeService();
		shutReq.setNodeName("First");
		ShutdownNodeResponse shutResp = (ShutdownNodeResponse) client.doRequest(shutReq);
		assertEquals(ErrorCode.Success, shutResp.Code);
	}

	@Test
	public void testConnectNodeService() {
		CreateNodeService request = new CreateNodeService(); 
		request.setName("N1");
		request.setPort(8070);
		
		RestClient client = new RestClient();
		CreateNodeResponse resp = (CreateNodeResponse) client.doRequest(request);
		assertEquals(ErrorCode.Success, resp.Code);

		request.setName("N2");
		request.setPort(8071);
		resp = (CreateNodeResponse) client.doRequest(request);
		assertEquals(ErrorCode.Success, resp.Code);

		ConnectNodeService cReq = new ConnectNodeService();
		cReq.setNodeName("N2");
		cReq.setParentName("N1");
		ConnectNodeResponse cResp = (ConnectNodeResponse) client.doRequest(cReq);
		assertEquals(ErrorCode.Success, cResp.Code);

		ShutdownNodeService shutReq = new ShutdownNodeService();
		shutReq.setNodeName("N1");
		ShutdownNodeResponse shutResp = (ShutdownNodeResponse) client.doRequest(shutReq);
		assertEquals(ErrorCode.Success, shutResp.Code);

		shutReq.setNodeName("N2");
		shutResp = (ShutdownNodeResponse) client.doRequest(shutReq);
		assertEquals(ErrorCode.Success, shutResp.Code);
	}

	@Test
	public void testStoreContentService() {
		ShutdownAllNodesService shutallReq = new ShutdownAllNodesService(); 
		shutallReq.setSaveState(true);
		
		RestClient client = new RestClient();
		ShutdownAllNodesResponse shutallResp = (ShutdownAllNodesResponse) client.doRequest(shutallReq);
		assertEquals(ErrorCode.Success, shutallResp.Code);
		
		CreateNodeService request = new CreateNodeService(); 
		request.setName("SC1");
		request.setPort(7070);
		
		CreateNodeResponse resp = (CreateNodeResponse) client.doRequest(request);
		assertEquals(ErrorCode.Success, resp.Code);
		
		StoreContentService scs = new StoreContentService();
		scs.setContent("Hello Kad");
		scs.setNodeName("SC1");		
		StoreContentResponse scsResp = (StoreContentResponse) client.doRequest(scs);
		assertEquals(ErrorCode.Success, scsResp.Code);
		
		GetContentService gcs = new GetContentService();
		gcs.setNodeName("SC1");
		gcs.setKey(scsResp.getContent().getKey());
		gcs.setOwnerId(scsResp.getContent().getOwnerId());
		GetContentResponse gcsResp = (GetContentResponse) client.doRequest(gcs);
		assertEquals(ErrorCode.Success, gcsResp.Code);
		
		ShutdownNodeService shutReq = new ShutdownNodeService();
		shutReq.setNodeName("SC1");
		ShutdownNodeResponse shutResp = (ShutdownNodeResponse) client.doRequest(shutReq);
		assertEquals(ErrorCode.Success, shutResp.Code);
	}

	@Test
	public void testGetNodeInfo() {
		RestClient client = new RestClient();
		
		CreateNodeService request = new CreateNodeService(); 
		request.setName("SC1");
		request.setPort(7070);
		
		CreateNodeResponse resp = (CreateNodeResponse) client.doRequest(request);
		assertEquals(ErrorCode.Success, resp.Code);
		
		ListNodesService lreq = new ListNodesService();
		ListNodesResponse lresp = (ListNodesResponse) client.doRequest(lreq);
		assertEquals(ErrorCode.Success, lresp.Code);
		
		GetNodeInfoService greq = new GetNodeInfoService();
		greq.setNodeName("SC1");
		GetNodeInfoResponse gresp = (GetNodeInfoResponse) client.doRequest(greq);
		assertEquals(ErrorCode.Success, gresp.Code);
		
		GetNodeRouteService grreq = new GetNodeRouteService();
		grreq.setNodeName("SC1");
		GetNodeRouteResponse grresp = (GetNodeRouteResponse) client.doRequest(grreq);
		assertEquals(ErrorCode.Success, grresp.Code);
		
		GetStorageInfoService gsis = new GetStorageInfoService();
		gsis.setNodeName("SC1");
		GetStorageInfoResponse gsiresp = (GetStorageInfoResponse) client.doRequest(gsis);
		assertEquals(ErrorCode.Success, gsiresp.Code);
		
		ShutdownNodeService shutReq = new ShutdownNodeService();
		shutReq.setNodeName("SC1");
		ShutdownNodeResponse shutResp = (ShutdownNodeResponse) client.doRequest(shutReq);
		assertEquals(ErrorCode.Success, shutResp.Code);
	}
}
