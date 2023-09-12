package com.badsmell.service;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import com.badsmell.base.Factor;
import com.badsmell.context.FinalContext;
import com.badsmell.context.UnevenSvcContext;
import com.badsmell.service.AvailabilityService;
import com.badsmell.utils.ServiceFactor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-09 20:40
 */
@Service
public class UnevenService {

    @Autowired
    public AvailabilityService availabilityService;
    @Autowired
    public FinalContext finalContext;

    public UnevenSvcContext getUnevenService(List<String> result){
        List<ReceivedLog> receivedLogList = availabilityService.getReceivedLogList(result);
        Factor factor = ServiceFactor.getSvcFactor(receivedLogList);
        double sum = 0.0;
        int size = factor.getSvcFactor().size();
        for(String svc : factor.getSvcFactor().keySet()){
                sum += factor.getSvcFactor().get(svc);
        }
        double avg = sum/size;
        System.out.println("avg"+avg);
        double quadraticSum = 0.0;
        for(String svc : factor.getSvcFactor().keySet()){
            quadraticSum += Math.pow(factor.getSvcFactor().get(svc) - avg, 2);
        }
        double std = Math.sqrt(quadraticSum/size);
        System.out.println("Std"+ std);
        double coverage = 0.0;
        List<String> unevenSvcList = new ArrayList<>();
        for(String svc : factor.getSvcFactor().keySet()){
            double count = factor.getSvcFactor().get(svc);
            if(count - avg > 3 * std){
                unevenSvcList.add(svc);
                coverage += count/ factor.getSum();
            }
        }
        UnevenSvcContext unevenSvcContext = new UnevenSvcContext(1 - coverage, unevenSvcList);
        finalContext.setUnevenSvcContext(unevenSvcContext);
        if(!unevenSvcContext.getServiceList().isEmpty())
            unevenSvcContext.setStatus(true);
        return unevenSvcContext;
    }
}
