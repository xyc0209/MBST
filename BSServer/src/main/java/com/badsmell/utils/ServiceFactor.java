package com.badsmell.utils;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import com.badsmell.base.Factor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: get the factor of svc.
 * @author: xyc
 * @date: 2023-03-09 19:40
 */
public class ServiceFactor {

    public static Factor getSvcFactor(List<ReceivedLog> receivedLogList){
        Map<String, Integer> svcFactorList = new HashMap<>();
        for(ReceivedLog receivedLog: receivedLogList){
            if(receivedLog.getLogType().equals("FUNCTION_CALL")){
                String svc = receivedLog.getServiceName();
                svcFactorList.put(svc, svcFactorList.getOrDefault(svc, 0)+1);
            }
        }
        int sum = 0;
        for(String svc: svcFactorList.keySet()){
            sum+=svcFactorList.get(svc);
        }
        System.out.println("sum"+sum);
        System.out.println("travel "+ svcFactorList.get("travelservice"));
        System.out.println("route "+ svcFactorList.get("routeservice"));
        Factor factor = new Factor(sum, svcFactorList);
        return factor;
    }
}
