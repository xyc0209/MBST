package com.badsmell.service;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import com.badsmell.analysisentity.cdabs.ReceivedMetricLog;
import com.badsmell.base.Factor;
import com.badsmell.context.UnevenResContext;
import com.badsmell.context.FinalContext;
import com.badsmell.utils.ServiceFactor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-10 14:27
 */
@Service
public class UnevenResService {

    @Autowired
    public AvailabilityService availabilityService;
    @Autowired
    public FinalContext finalContext;

    public UnevenResContext getUnevenResService(List<String> result, List<String> callResult){
        List<ReceivedLog> receivedLogList = availabilityService.getReceivedLogList(callResult);
        Factor factor = ServiceFactor.getSvcFactor(receivedLogList);
        Map<String, Map<String, List<ReceivedMetricLog>>> instanceLogCount = new HashMap<>(); //used to store logs of per container
        Map<String, Map<String, ReceivedMetricLog>> instanceCount = new HashMap<>();
        Map<String, Map<String, ReceivedMetricLog>> unevenCpuInstance = new HashMap<>();
        Map<String, Map<String, ReceivedMetricLog>> unevenRamInstance = new HashMap<>();
        for(int i = 0; i<result.size(); i++){
            ReceivedMetricLog receivedMetricLog = new ReceivedMetricLog();
            receivedMetricLog.resolveFromString(result.get(i));
            String podName = receivedMetricLog.getPodName();
            String svcName = podName.substring(0, podName.lastIndexOf("service") + 7);
            if(factor.getSvcFactor().containsKey(svcName)){
                if(!instanceCount.containsKey(svcName)) {
                    instanceCount.put(svcName, new HashMap<>());
                    instanceLogCount.put(svcName, new HashMap<>());
                }
                if(!instanceLogCount.get(svcName).containsKey(podName))
                    instanceLogCount.get(svcName).put(podName, new ArrayList<>());
                instanceLogCount.get(svcName).get(podName).add(receivedMetricLog);
                if(!instanceCount.get(svcName).containsKey(podName))
                    instanceCount.get(svcName).put(podName, receivedMetricLog);
            }
        }
        double cpuContainerAvg= 0.0;
        double ramContainerAvg = 0.0;
        for(String svc: instanceLogCount.keySet()){
            for(String pod: instanceLogCount.get(svc).keySet()){
                for(ReceivedMetricLog log: instanceLogCount.get(svc).get(pod)){
                    cpuContainerAvg += Double.valueOf(log.getCpuUsage());
                    ramContainerAvg += Double.valueOf(log.getRamUsage());
                }
                cpuContainerAvg /= instanceLogCount.get(svc).get(pod).size();
                ramContainerAvg /= instanceLogCount.get(svc).get(pod).size();
                instanceCount.get(svc).get(pod).setCpuUsage(String.valueOf(cpuContainerAvg));
                instanceCount.get(svc).get(pod).setRamUsage(String.valueOf(ramContainerAvg));
            }
        }
        Double cpuSum = 0.0;
        Double ramSum = 0.0;
        int containerCount = 0;
        for(String svc: instanceCount.keySet()){
            containerCount +=instanceCount.get(svc).size();
            for(String podName: instanceCount.get(svc).keySet()){
                cpuSum += Double.valueOf(instanceCount.get(svc).get(podName).getCpuUsage());
                ramSum += Double.valueOf(instanceCount.get(svc).get(podName).getRamUsage());
            }
        }
        Double cpuAvg = cpuSum/containerCount;
        Double ramAvg = ramSum/containerCount;
        double cpuQuadraticSum = 0.0;
        double ramQuadraticSum = 0.0;
        for(String svc: instanceCount.keySet()){
            containerCount +=instanceCount.get(svc).size();
            for(String podName: instanceCount.get(svc).keySet()){
                cpuQuadraticSum += Math.pow(Double.valueOf(instanceCount.get(svc).get(podName).getCpuUsage()) - cpuAvg.doubleValue(), 2);
                ramQuadraticSum += Math.pow(Double.valueOf(instanceCount.get(svc).get(podName).getRamUsage()) - ramAvg.doubleValue(), 2);
            }
        }
        double cpuStd = cpuQuadraticSum/containerCount;
        double ramStd = ramQuadraticSum/containerCount;
        double cpuCoverage = 0.0;
        double ramCoverage = 0.0;
        for(String svc: instanceCount.keySet()){
            for(String podName: instanceCount.get(svc).keySet()){
                ReceivedMetricLog receivedMetricLog = instanceCount.get(svc).get(podName);
                if(Double.valueOf(instanceCount.get(svc).get(podName).getCpuUsage()) - cpuAvg > 3 * cpuStd){
                    if(!unevenCpuInstance.containsKey(svc))
                        unevenCpuInstance.put(svc, new HashMap<>());
                    unevenCpuInstance.get(svc).put(podName, receivedMetricLog);
                }
                if(Double.valueOf(instanceCount.get(svc).get(podName).getRamUsage()) - ramAvg > 3 * ramStd){
                    if(!unevenRamInstance.containsKey(svc))
                        unevenRamInstance.put(svc, new HashMap<>());
                    unevenRamInstance.get(svc).put(podName, receivedMetricLog);
                }
            }
            if(factor.getSvcFactor().containsKey(svc)) {
                if (unevenCpuInstance.containsKey(svc) && unevenCpuInstance.get(svc).size() != 0) {
                    cpuCoverage += factor.getSvcFactor().get(svc).doubleValue() / factor.getSum() * ((double) unevenCpuInstance.get(svc).size() / instanceCount.get(svc).size());
                }
                if (unevenRamInstance.containsKey(svc) && unevenRamInstance.get(svc).size() != 0) {
                    ramCoverage += factor.getSvcFactor().get(svc).doubleValue() / factor.getSum() * ((double) unevenRamInstance.get(svc).size() / instanceCount.get(svc).size());
                }
            }
        }
        System.out.println("cpuCoverage"+cpuCoverage);
        System.out.println("ramCoverage"+ramCoverage);
        UnevenResContext unevenResContext = new UnevenResContext(1- cpuCoverage, 1 - ramCoverage, unevenCpuInstance, unevenRamInstance);
        finalContext.setUnevenResContext(unevenResContext);
        if(!unevenResContext.getUnevenCpuInstance().isEmpty() || !unevenResContext.getUnevenRamInstance().isEmpty())
            unevenResContext.setStatus(true);
        return unevenResContext;

    }
}
