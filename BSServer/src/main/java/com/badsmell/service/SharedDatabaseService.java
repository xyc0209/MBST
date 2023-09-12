package com.badsmell.service;

import com.badsmell.base.MyLogger;
import com.badsmell.context.RTIncreasedContext;
import com.badsmell.context.ServiceAvailabilityContext;
import com.badsmell.base.ServiceRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: Determine whether to allow sharing of databases
 * @author: xyc
 * @date: 2023-03-22 11:07
 */
@Service
public class SharedDatabaseService {
    @Autowired
    public AvailabilityService availabilityService;
    @Autowired
    public CaculateService caculateService;

    private static final double times = 2;
    public RTIncreasedContext judgeAllowed(List<String> currentWindowResult, List<String> lastWindowResult, MyLogger logger) throws IOException {

        //get current Window Services RT
        availabilityService.getReceivedLogList(currentWindowResult);
        List<ServiceAvailabilityContext> responseContext = availabilityService.getResponseContext(logger);
        //get last Window Services RT
        availabilityService.getReceivedLogList(lastWindowResult);
        List<ServiceAvailabilityContext> lastResponseContext = availabilityService.getResponseContext(logger);
        System.out.println("responseSize"+responseContext.size());
//        for(ServiceAvailabilityContext s: responseContext){
//            System.out.println(s.toString());
//        }
//        for(ServiceAvailabilityContext s: lastResponseContext){
//            System.out.println(s.toString());
//        }
        RTIncreasedContext rtIncreasedContext = new RTIncreasedContext();
        for(ServiceAvailabilityContext svc1: responseContext){
            for(ServiceAvailabilityContext svc2: lastResponseContext){
                if(svc1.getServiceName().equals(svc2.getServiceName()) && svc1.getRtInMillis() >= svc2.getRtInMillis() * times && svc1.getRtInMillis() > 1500 )
                    logger.log("--------sharedDatabase------------");
                    rtIncreasedContext.getRtIncreasedSvcLists().add(new ServiceRT(svc1.getServiceName(), svc1.getRtInMillis(), svc2.getRtInMillis()));
            }
        }
        Set<String> highRTService = new HashSet<>();
        for(ServiceRT serviceRT: rtIncreasedContext.getRtIncreasedSvcLists()){
            if(caculateService.getSharedDatabaseSet().contains(serviceRT.getServiceName()))
                highRTService.add(serviceRT.getServiceName());
        }
        if(!highRTService.isEmpty())
            logger.log("sharedDatabase"+highRTService.toString());
        caculateService.setSharedDatabaseSet(highRTService);
        return rtIncreasedContext;


    }
}
