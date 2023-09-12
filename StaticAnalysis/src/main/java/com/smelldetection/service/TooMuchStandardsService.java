package com.smelldetection.service;

import com.smelldetection.base.context.TMSContext;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.utils.TooMuchStandardAnalysisUtils;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xyc
 * @date: 2023-02-26 20:37
 */
@Service
public class TooMuchStandardsService {

    public TMSContext getTMSServices(RequestItem request){
        String path = request.getServicesPath();
        TMSContext tmsContext = new TMSContext();
        TooMuchStandardAnalysisUtils.analysisTMSABS("/root/test-lin/test/experiment/experiment", tmsContext);
        if(tmsContext.getValueMap().getIsMussy())
            tmsContext.setStatus(true);
        return tmsContext;


    }
}
