package com.mbs.common.utils;

import com.mbs.common.base.MClusterConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MUrlUtils {

    private final static Logger logger = LogManager.getLogger(MUrlUtils.class);

    /**
     * Construct the uri to fetch all the instance info in the cluster
     * @return URI
     */
    public static URI getMclusterFetchInstanceInfoUri(String ipAddr, int port) {
            return MUrlUtils.getRemoteUri(
                    ipAddr,
                    port,
                    MClusterConfig.MCLUSTER_FETCH_INSTANCE_INFO
            );
    }

    /**
     * Construct the uri to fetch the parentIdMap for given INSTANCE IP ADDRESS
     * @param instanceIpAddr: ip address of given instance
     * @param port: service port
     * @return URI
     */
    public static URI getMClusterAgentFetchClientInfoUri(String instanceIpAddr, int port) {
        return MUrlUtils.getRemoteUri(instanceIpAddr, port, MClusterConfig.MCLUSTERAGENT_FETCH_CLIENT_INFO);
    }

    public static URI getMClientRequestRemoteUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MCLUSTERAGENT_REQUEST_REMOTE_URI);
    }

    public static URI getMClientAgentSetRestInfoUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MCLUSTERAGENT_SET_REST_INFO);
    }

    public static URI getMServerRemoteUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MClusterConfig.MSERVER_GET_REMOTE_URI);
    }

    public static URI getMServerLoadInstanceInfoUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MClusterConfig.MSERVER_LOAD_INSTANCEINFO);
    }

    public static URI getBuildCenterBuildUri(String buildCenterIpAddr, int buildCenterPort) {
        return MUrlUtils.getRemoteUri(buildCenterIpAddr, buildCenterPort, MClusterConfig.BUILD_CENTER_BUILD_URI);
    }

    public static URI getBuildCenterCBuildUri(String buildCenterIpAddr, int buildCenterPort) {
        return MUrlUtils.getRemoteUri(buildCenterIpAddr, buildCenterPort, MClusterConfig.BUILD_CENTER_CBUILD_URI);
    }

    public static URI getMClientAgentDeployUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MCLUSTERAGENT_DEPLOY_URI);
    }

    public static URI getMServerNotifyJobUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MClusterConfig.MSERVER_JOB_NOTIFY_URI);
    }

    public static URI getMServerDeployNotifyJobUri(String serverIpAddr, int serverPort) {
        return MUrlUtils.getRemoteUri(serverIpAddr, serverPort, MClusterConfig.MSERVER_DEPLOY_JOB_NOTIFY_URI);
    }

    public static URI getMClientSetApiCStatus(String instanceIpAddr, int port) {
        return MUrlUtils.getRemoteUri(instanceIpAddr, port, MClusterConfig.MCLIENT_SET_APICS_URI);
    }

    public static URI getMClusterAgentSetApiCStatus(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MCLUSTERAGENT_SET_APICS_URI);
    }

    public static URI getMClusterAgentFetchLogsByTime(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MCLUSTERAGENT_FETCH_LOGS);
    }

    public static URI getMClusterAgentDeleteInstanceUri(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MCLUSTERAGENT_DELETE_URI);
    }

    public static URI getMServerFetchRequestUrl(String clusterAgentIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterAgentIpAddr, port, MClusterConfig.MSERVER_FETCH_REQUEST_URL);
    }

    public static URI getMClusterFetchRequestUrl(String clusterIpAddr, int port) {
        return MUrlUtils.getRemoteUri(clusterIpAddr, port, MClusterConfig.MCLUSTERAGNET_FETCH_REQUEST_URL);
    }

    public static URI getMGatewayUpdateUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MClusterConfig.MGATEWAY_UPDATE_URI);
    }

    public static URI getMGatewayAllUserUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MClusterConfig.MGATEWAY_ALL_USER_URI);
    }

    public static URI getMClusterAllUserUrl(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MClusterConfig.MCLUSTERAGENT_ALL_USER_URL);
    }

    public static URI getMClusterUpdateGateways(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MClusterConfig.MCLUSTERAGENT_UPDATE_GATEWAYS);
    }

    public static URI getMServerDoRequestUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MClusterConfig.MSERVER_DO_REQUEST_URL);
    }

    public static URI getMClusterDoRequestUri(String ipAddr, int port) {
        return MUrlUtils.getRemoteUri(ipAddr, port, MClusterConfig.MCLUSTERAGENT_DO_REQUEST_URL);
    }

    public static URI getRemoteUri(String ipAddr, int port, String path) {
        URI uri = null;
        try {
            uri = new URI(
                    "http",
                    null,
                    ipAddr,
                    port,
                    path, null, null
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        logger.debug(uri);
        return uri;
    }

    public static URI getRemoteUriWithQueries(URI oldUri, Map<String, String> paramMap) {
        URI uri = null;
        LinkedMultiValueMap<String, String> pMap = new LinkedMultiValueMap<>();
        for (String key : paramMap.keySet()) {
            List<String> vList = new ArrayList<>(1);
            vList.add(paramMap.get(key));
            pMap.put(key, vList);
        }
        try {
            uri = UriComponentsBuilder.fromUri(oldUri).queryParams(pMap).build().toUri();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static URI getMClusterSetRestInfoUri(String instanceIpAddr, int port) {
        return MUrlUtils.getRemoteUri(instanceIpAddr, port, MClusterConfig.MCLUSTER_SET_REST_INFO);
    }
}
