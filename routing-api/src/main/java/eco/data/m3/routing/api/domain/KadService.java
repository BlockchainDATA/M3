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

package eco.data.m3.routing.api.domain;

import eco.data.m3.routing.JKademliaNode;
import eco.data.m3.routing.api.core.*;
import eco.data.m3.routing.api.rest.Common;
import eco.data.m3.routing.api.rest.RestClient;
import eco.data.m3.routing.api.rest.request.IndexDataListService;
import eco.data.m3.routing.api.rest.response.IndexDataListResponse;
import eco.data.m3.routing.api.utils.CHexConvert;
import eco.data.m3.routing.api.utils.OSinfo;
import eco.data.m3.routing.dht.GetParameter;
import eco.data.m3.routing.dht.KademliaStorageEntry;
import eco.data.m3.routing.exceptions.ContentNotFoundException;
import eco.data.m3.routing.exceptions.RoutingException;
import eco.data.m3.routing.node.KademliaId;
import eco.data.m3.routing.node.Node;
import eco.data.m3.routing.routing.KademliaRoutingTable;
import eco.data.m3.routing.simulations.DHTContentImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
* @author: xquan
* Main Routing Service, Wrap all Kademlia api into a service interface.
* @since: Created in 2018-6-29
**/
public class KadService {

    HashMap<String, JKademliaNode> nodes = new HashMap<String, JKademliaNode>();

    private static final KadService singleton = new KadService();

    public static KadService getInstance() {
        return singleton;
    }

    /**
     *
     */
    public KadService() {
        //TODO, loadNodesFromDisk, use for persistance;
    }

    public String getStorageRoot() {
        String cache_path = "";
        if (OSinfo.isWindows()) {

        } else if (OSinfo.isLinux()) {
            cache_path = "/root/kademlia/";
        } else
            cache_path = null;
        return cache_path;
    }

    private void loadNodesFromDisk() {
        String cache_path = getStorageRoot();
        if (null == cache_path)
            return;

    }

    //TODO
    private void saveNodesToDisk() {

    }

    public ErrorCode createNode(String name, String kid, int port) {
        if (nodes.containsKey(name))
            return ErrorCode.NameAlreadyExsit;

        try {
            JKademliaNode kad = null;
            if (kid != null)
                kad = new JKademliaNode(name, new KademliaId(kid), port);
            else
                kad = new JKademliaNode(name, new KademliaId(), port);
            nodes.put(name, kad);
        } catch (IllegalArgumentException ie) {
            return ErrorCode.ParameterError;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.PortAlreadyBind;
        }
        return ErrorCode.Success;
    }

    public ErrorCode loadNode(String name) {
        try {
            JKademliaNode kad = JKademliaNode.loadFromFile(name);
            nodes.put(name, kad);
        } catch (FileNotFoundException e) {
            return ErrorCode.NodeNotFound;
        } catch (ClassNotFoundException e) {
            return ErrorCode.ServiceError;
        } catch (IOException e) {
            return ErrorCode.ServiceError;
        }
        return ErrorCode.Success;
    }


    public ErrorCode connectNode(String nodeName, String parentName) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return ErrorCode.NodeNotFound;
        JKademliaNode parentNode = nodes.get(parentName);
        if (parentNode == null)
            return ErrorCode.NodeParentNotFound;
        try {
            node.bootstrap(parentNode.getNode());
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.ServiceError;
        }
        return ErrorCode.Success;
    }

    public ErrorCode connectNode(String nodeName, String kid, String inetaddr, int port) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return ErrorCode.NodeNotFound;

        long to = node.getCurrentConfiguration().operationTimeout();

        byte[] keyb = CHexConvert.hexStr2Bytes(kid);
        if (keyb.length != 20) {
            System.out.println(kid + " -- " + keyb.length);
            return ErrorCode.ParameterError;
        }

        try {
            Node parentNode = new Node(new KademliaId(keyb), InetAddress.getByName(inetaddr), port);
            node.bootstrap(parentNode);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return ErrorCode.UnknownHost;
        } catch (RoutingException e) {
            e.printStackTrace();
            return ErrorCode.RoutingError;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.ServiceError;
        }
        return ErrorCode.Success;
    }

    public ErrorCode shutdownAllNodes(boolean saveState) {
        for (JKademliaNode node : nodes.values()) {
            try {
                node.shutdown(saveState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nodes.clear();
        return ErrorCode.Success;
    }

    public ErrorCode shutdownNode(String nodeName, boolean saveState) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return ErrorCode.NodeNotFound;
        try {
            node.shutdown(saveState);
            nodes.remove(nodeName);
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.ServiceError;
        }
        return ErrorCode.Success;
    }

    private NodeInfo buildNodeInfo(JKademliaNode node) {
        NodeInfo info = new NodeInfo();
        info.setName(node.getOwnerId());
        info.setPort(node.getPort());
        info.setNodeId(node.getNode().getNodeId().toString());

        if (node.getParentNode() != null) {
//			info.setParentIp(node.get);
            info.setParentPort(node.getParentNode().getSocketAddress().getPort());
            info.setParentIp(node.getParentNode().getSocketAddress().getAddress().getHostAddress());
            info.setParentNodeId(node.getParentNode().getNodeId().toString());

        }

        info.setBootStrapTime(node.getStatistician().getBootstrapTime());
        info.setTotalDataReceived(node.getStatistician().getTotalDataReceived());
        info.setTotalDataSent(node.getStatistician().getTotalDataSent());
        info.setAverageContentLookupRouteLength(node.getStatistician().averageContentLookupRouteLength());
        info.setAverageContentLookupTime(node.getStatistician().averageContentLookupTime());
        info.setNumContentLookups(node.getStatistician().numContentLookups());
        info.setNumFailedContentLookups(node.getStatistician().numFailedContentLookups());
        return info;
    }

    public NodeInfo getNodeInfo(String nodeName) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return null;
        return buildNodeInfo(node);
    }

    public StorageInfo getStorageInfo(String nodeName) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return null;
        StorageInfo info = new StorageInfo();
        info.setContent(node.getDHT().toString());
        info.setCode(ErrorCode.Success);
        return info;
    }

    public List<NodeInfo> listNodes() {
        List<NodeInfo> results = new ArrayList<>();
        for (JKademliaNode node : nodes.values()) {
            results.add(buildNodeInfo(node));
        }
        return results;
    }

    private RouteInfo buildRouteInfo(JKademliaNode node) {
        KademliaRoutingTable table = node.getRoutingTable();
        RouteInfo info = new RouteInfo();
        info.setContent(table.toString());
        return info;
    }

    public RouteInfo getNodeRoute(String nodeName) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return null;
        return buildRouteInfo(node);
    }

    private void updateContentInfo(DHTContentImpl c, ContentInfo info) {
        info.setOwnerId(c.getOwnerId());
        info.setKey(c.getKey().toString());
        info.setType(c.getType());
        info.setCreatedTimestamp(new Date(c.getCreatedTimestamp() * 1000));
        info.setLastUpdatedTimestamp(new Date(c.getLastUpdatedTimestamp() * 1000));
        info.setContent(c.getData());
    }

    public ContentInfo storeContent(String nodeName, String content) {
        ContentInfo info = new ContentInfo();
        JKademliaNode node = nodes.get(nodeName);
        if (node == null) {
            info.setCode(ErrorCode.NodeNotFound);
            return info;
        }

        DHTContentImpl c = new DHTContentImpl(node.getOwnerId(), content);
        try {
            node.put(c);
        } catch (IOException e) {
            e.printStackTrace();
            info.setCode(ErrorCode.ServiceError);
            return info;
        }
        updateContentInfo(c, info);
        info.setCode(ErrorCode.Success);

        return info;
    }


    public ContentInfo getContent(String nodeName, byte[] key, String ownerId) {
        ContentInfo info = new ContentInfo();
        JKademliaNode node = nodes.get(nodeName);
        if (node == null) {
            info.setCode(ErrorCode.NodeNotFound);
            return info;
        }

        try {
            GetParameter gp = new GetParameter(new KademliaId(key), DHTContentImpl.TYPE);
            gp.setOwnerId(ownerId);
            KademliaStorageEntry content = node.get(gp);
            DHTContentImpl c = new DHTContentImpl().fromSerializedForm(content.getContent());
            info.setCode(ErrorCode.Success);
            updateContentInfo(c, info);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            info.setCode(ErrorCode.ParameterError);
            return info;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            info.setCode(ErrorCode.NoSuchElement);
            return info;
        } catch (IOException e) {
            e.printStackTrace();
            info.setCode(ErrorCode.ServiceError);
            return info;
        } catch (ContentNotFoundException e) {
            e.printStackTrace();
            info.setCode(ErrorCode.ContentNotFound);
            return info;
        }

        return info;
    }

    public ErrorCode refreshNode(String nodeName) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return ErrorCode.NodeNotFound;

        try {
            node.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.ServiceError;
        }
        return ErrorCode.Success;
    }

    public ErrorCode saveState(String nodeName) {
        JKademliaNode node = nodes.get(nodeName);
        if (node == null)
            return ErrorCode.NodeNotFound;
        try {
            node.saveKadState();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.ServiceError;
        }
        return ErrorCode.Success;
    }

    public List<String> listFiles() {
        IndexDataListService req = new IndexDataListService();
        RestClient client = new RestClient(Common.INDEX_HOST);
        IndexDataListResponse resp = (IndexDataListResponse) client.doRequest(req);
        if (resp != null)
            return resp.getFiles();
        return new ArrayList<>();
    }
}
