package com.smelldetection.service;

import com.smelldetection.base.context.GodContext;
import com.smelldetection.base.context.WrongCutContext;
import com.smelldetection.base.item.RequestItem;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-27 19:14
 */
@Service
@NoArgsConstructor
public class GodService {
    @Autowired
    public WrongServiceCutService wrongServiceCutService;


    public GodContext getGodServices(RequestItem requestItem) throws IOException {
        WrongCutContext wrongCutContext= wrongServiceCutService.getServicesEntityCount(requestItem);
        double avgEntityCount = wrongServiceCutService.getAvgEntityCount(wrongCutContext);
        int size = wrongCutContext.getWrongCutMap().size();
        double quadraticSum = 0.0;
        for (String svc : wrongCutContext.getWrongCutMap().keySet()) {
            quadraticSum += Math.pow(wrongCutContext.getWrongCutMap().get(svc).get("entityCount").intValue() - avgEntityCount, 2);
        }
        double std = Math.sqrt(quadraticSum / size);
        GodContext godContext = new GodContext();
        for (String svc : wrongCutContext.getWrongCutMap().keySet()) {

            double entityCount = wrongCutContext.getWrongCutMap().get(svc).get("entityCount").intValue();
            if((entityCount >= 3) && (entityCount > avgEntityCount) && (Math.abs( entityCount- avgEntityCount) >= 3 * std)){
                godContext.getGodServiceMap().put(svc,wrongCutContext.getWrongCutMap().get(svc));
            }
        }
        if(!godContext.getGodServiceMap().isEmpty())
            godContext.setStatus(true);
        return godContext;

    }
}
