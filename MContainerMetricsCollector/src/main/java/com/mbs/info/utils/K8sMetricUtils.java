package com.mbs.info.utils;

import com.mbs.info.beans.node.MNodeMetrics;
import com.mbs.info.beans.node.MNodesMetricsResponse;
import com.mbs.info.beans.pod.MPodMetrics;
import com.mbs.info.beans.pod.MPodsMetricsResponse;
import com.mbs.common.utils.MRequestUtils;
import com.mbs.common.utils.MUrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

import static com.mbs.common.base.MClusterConfig.K8S_NAMESPACE;

@Component
public class K8sMetricUtils {

    public static String K8S_CLIENT_IP;
    public static Integer K8S_CLIENT_PORT;
    private static String PREFIX_METRICS_API = "/apis/metrics.k8s.io/v1beta1";
    private static String SUFFIX_METRICS_NODES = "/nodes";
    private static String SUFFIX_METRICS_PODS = "/pods";

    private static String getMetricsNodesUrl() {
        return PREFIX_METRICS_API + SUFFIX_METRICS_NODES;
    }

    private static String getMetricsPodsUrl() {
        return PREFIX_METRICS_API + SUFFIX_METRICS_PODS;
    }

    public static List<MNodeMetrics> getNodesMetrices() {
        List<MNodeMetrics> resultList = new ArrayList<>();
        MNodesMetricsResponse response = MRequestUtils.sendRequest(
                MUrlUtils.getRemoteUri(K8S_CLIENT_IP, K8S_CLIENT_PORT, getMetricsNodesUrl()),
                null,
                MNodesMetricsResponse.class,
                RequestMethod.GET
        );
        if (response != null) {
            resultList = response.getItems();
        }
        return resultList;
    }
    public static List<MPodMetrics> testPod() {
        List<MPodMetrics> resultList = new ArrayList<>();
        MPodsMetricsResponse response = MRequestUtils.sendRequest(
                MUrlUtils.getRemoteUri("10.245.1.233", 6443, getMetricsPodsUrl()),
                null,
                MPodsMetricsResponse.class,
                RequestMethod.GET
        );
        if (response != null) {
            resultList = response.getItems();
        }
        resultList.removeIf(podMetrics -> !podMetrics.getMetadata().getNamespace().equals(K8S_NAMESPACE));
        return resultList;
    }
    public static List<MPodMetrics> getPodsMetrics() {
        List<MPodMetrics> resultList = new ArrayList<>();
        MPodsMetricsResponse response = MRequestUtils.sendRequest(
                MUrlUtils.getRemoteUri(K8S_CLIENT_IP, K8S_CLIENT_PORT, getMetricsPodsUrl()),
                null,
                MPodsMetricsResponse.class,
                RequestMethod.GET
        );
        if (response != null) {
            resultList = response.getItems();
        }
        resultList.removeIf(podMetrics -> !podMetrics.getMetadata().getNamespace().equals(K8S_NAMESPACE));
        return resultList;
    }

    public static void main(String[] args) {
        System.out.println(testPod().toString());
    }

}
