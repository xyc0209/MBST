package com.badsmell.service;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import com.badsmell.base.Factor;
import com.badsmell.context.UnevenIfcContext;
import com.badsmell.context.FinalContext;
import com.badsmell.utils.ServiceFactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-09 21:31
 */
@Service
public class UnevenInterfaceService {

    @Autowired
    public AvailabilityService availabilityService;
    @Autowired
    public FinalContext finalContext;

    public UnevenIfcContext getUnevenInterface(List<String> result){
        Map<String, Map<String,Integer>> svcInterfaceMap = new HashMap<>();
        Map<String, List<String>> unevenInterface = new HashMap<>();
        List<ReceivedLog> receivedLogList = availabilityService.getReceivedLogList(result);
        Factor factor = ServiceFactor.getSvcFactor(receivedLogList);
        for(ReceivedLog receivedLog: receivedLogList){
            String service = receivedLog.getServiceName();
            String ifc = receivedLog.getLogMethodName();
            if(!svcInterfaceMap.containsKey(service))
                svcInterfaceMap.put(service, new HashMap<>());
            svcInterfaceMap.get(service).put(ifc, svcInterfaceMap.get(service).getOrDefault(ifc, 0) + 1);
        }
        for(String svc: svcInterfaceMap.keySet()){
            int interfaceCount = svcInterfaceMap.get(svc).size();
            double sum=0.0;
            for(String ifc: svcInterfaceMap.get(svc).keySet()){
                sum += svcInterfaceMap.get(svc).get(ifc);
            }
            int size = svcInterfaceMap.get(svc).size();
            double avg = sum / size;
            double quadraticSum = 0.0;
            for(String ifc: svcInterfaceMap.get(svc).keySet()){
                quadraticSum += Math.pow(svcInterfaceMap.get(svc).get(ifc) - avg, 2);
            }
            double std = Math.sqrt(quadraticSum/ size);

            for(String ifc : svcInterfaceMap.get(svc).keySet()){
                double count = svcInterfaceMap.get(svc).get(ifc);
                if(count - avg > 3 * std){
                    if(!unevenInterface.containsKey(svc))
                        unevenInterface.put(svc, new ArrayList<>());
                    unevenInterface.get(svc).add((ifc));
                }
            }
        }
        double coverage = 0.0;
        for(String svc: unevenInterface.keySet()){
            double unevenInterfaceCount = unevenInterface.get(svc).size();
            double interfaceCount = svcInterfaceMap.get(svc).size();
            coverage +=(unevenInterfaceCount / interfaceCount) * (factor.getSvcFactor().get(svc).doubleValue() / factor.getSum());
        }
        UnevenIfcContext unevenIfcContext = new UnevenIfcContext(1 - coverage,unevenInterface);
        finalContext.setUnevenIfcContext(unevenIfcContext);
        if(!unevenIfcContext.getUnevenInterface().isEmpty())
            unevenIfcContext.setStatus(true);
        return unevenIfcContext;

    }


}
