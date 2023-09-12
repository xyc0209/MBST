package com.badsmell.service;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import com.badsmell.base.MyLogger;
import com.badsmell.context.AvailabilityContext;
import com.badsmell.base.Factor;
import com.badsmell.context.ServiceAvailabilityContext;
import com.badsmell.context.FinalContext;
import com.badsmell.utils.ServiceFactor;

import org.joda.time.DateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-08 10:53
 */
@Service
public class AvailabilityService {

    public Map<String, Map<String,ArrayList<DateTime>>> serviceMap;
    @Autowired
    public FinalContext finalContext;

    public AvailabilityService() {

    }
    public AvailabilityContext findLowAvailability(List<String> result, MyLogger logger) throws IOException {
        List<ReceivedLog> receivedLogList = this.getReceivedLogList(result);
        long threshold = 2000l;
        List<ServiceAvailabilityContext> responseContext = getResponseContext(logger);
        Factor factor = ServiceFactor.getSvcFactor(receivedLogList);
        double coverage = 0;
        List<ServiceAvailabilityContext> svcWithLowAvaliability = new ArrayList<>();
        for(ServiceAvailabilityContext serviceAvailabilityContext: responseContext){
            if(serviceAvailabilityContext.getRtInMillis() >= threshold){
                svcWithLowAvaliability.add(serviceAvailabilityContext);
                coverage += factor.getSvcFactor().get(serviceAvailabilityContext.getServiceName()).doubleValue() / factor.getSum();
            }
        }
        AvailabilityContext availabilityContext = new AvailabilityContext(1 - coverage, svcWithLowAvaliability);
        finalContext.setAvailabilityContext(availabilityContext);
        if(!availabilityContext.getSvcWithLowAvailability().isEmpty())
            availabilityContext.setStatus(true);
        logger.log("availabilityContext"+availabilityContext.toString());
        return availabilityContext;


    }

    public List<ServiceAvailabilityContext> getResponseContext( MyLogger logger) throws IOException {
        List<ServiceAvailabilityContext> responseContext = new ArrayList<>();
        System.out.println("size" +serviceMap.size());
        Map<String, Integer> svc90ResponseTime = new HashMap<>();
        for(String svc: serviceMap.keySet()){
            List<Long> responseTime = new ArrayList<>();
            Map<String,ArrayList<DateTime>> svcTime = serviceMap.get(svc);
            for(String id: svcTime.keySet()){
                List<DateTime> timeList = svcTime.get(id);
                if(timeList.size() < 2)
                    continue;
                //Period period = new Period(timeList.get(0), timeList.get(0));
                // response time in milliseconds
                responseTime.add(Math.abs(timeList.get(0).getMillis() - timeList.get(1).getMillis()));
            }
            Collections.sort(responseTime);
            // 90% RT
            int index = (int) (Math.floor(responseTime.size() * 0.9));
            if(responseTime.size() == 0)
                return responseContext;
            ServiceAvailabilityContext serviceAvailabilityContext = new ServiceAvailabilityContext(svc, responseTime.get(index));
            responseContext.add(serviceAvailabilityContext);

        }
        logger.log("res"+ responseContext.toString());
        return responseContext;
    }
    public List<ServiceAvailabilityContext> getResponseContext() throws IOException {
        List<ServiceAvailabilityContext> responseContext = new ArrayList<>();
        System.out.println("size" +serviceMap.size());
        Map<String, Integer> svc90ResponseTime = new HashMap<>();
        for(String svc: serviceMap.keySet()){
            List<Long> responseTime = new ArrayList<>();
            Map<String,ArrayList<DateTime>> svcTime = serviceMap.get(svc);
            for(String id: svcTime.keySet()){
                List<DateTime> timeList = svcTime.get(id);
                if(timeList.size() < 2)
                    continue;
                //Period period = new Period(timeList.get(0), timeList.get(0));
                // response time in milliseconds
                responseTime.add(Math.abs(timeList.get(0).getMillis() - timeList.get(1).getMillis()));
            }
            Collections.sort(responseTime);
            // 90% RT
            int index = (int) (Math.floor(responseTime.size() * 0.9));
            if(responseTime.size() == 0)
                return responseContext;
            ServiceAvailabilityContext serviceAvailabilityContext = new ServiceAvailabilityContext(svc, responseTime.get(index));
            responseContext.add(serviceAvailabilityContext);

        }
        return responseContext;
    }
    public List<ReceivedLog> getReceivedLogList(List<String> result){
        this.serviceMap = new HashMap<>();
        List<ReceivedLog> receivedLogList = new ArrayList<>();
        for(int i = 0; i< result.size(); i++){
            ReceivedLog tempReceivedLog = new ReceivedLog();
            Boolean success = tempReceivedLog.resolveFromString(result.get(i));
            if (!success) {
                System.out.println("Log parse falied!!");
            }
            String serviceName = tempReceivedLog.getServiceName();
            if(!serviceMap.containsKey(serviceName)){
                serviceMap.put(serviceName, new HashMap<>());
            }
            String id = tempReceivedLog.getId();
            if(!serviceMap.get(serviceName).containsKey(id)){
                serviceMap.get(serviceName).put(id, new ArrayList<>());
            }
            serviceMap.get(serviceName).get(id).add(tempReceivedLog.getLogDateTime());
            receivedLogList.add(tempReceivedLog);
        }
        return receivedLogList;
    }
}
