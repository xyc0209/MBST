package com.badsmell.service;

import com.badsmell.context.FinalContext;
import com.smelldetection.base.context.SystemContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-30 15:54
 */
@Service
public class FinalService {
    @Autowired
    public FinalContext finalContext;
    public void setFinalContext(SystemContext systemContext, CaculateService caculateService){
        finalContext.setServicesCount(CaculateService.servicesCount);
        finalContext.setServiceList(CaculateService.serviceList);
        finalContext.setQualityScore(CaculateService.qualityScore);
        finalContext.setQualityScoreDetail(CaculateService.qualityScoreDetail);
        finalContext.setRuntimeQualityScore(CaculateService.runtimeQualityScore);
        finalContext.setSharedDatabaseSet(caculateService.sharedDatabaseSet);
        finalContext.setServiceIntimacySet(caculateService.serviceIntimacySet);
        finalContext.setApiVersionContext(systemContext.getApiVersionContext());
        finalContext.setCyclicReferenceContext(systemContext.getCyclicReferenceContext());
        finalContext.setEsbServiceContext(systemContext.getEsbServiceContext());
        finalContext.setGateWayContext(systemContext.getGateWayContext());
        finalContext.setGodContext(systemContext.getGodContext());
        finalContext.setGreedyContext(systemContext.getGreedyContext());
        finalContext.setHardCodeContext(systemContext.getHardCodeContext());
        finalContext.setHubContext(systemContext.getHubContext());
        finalContext.setMultiPathContext(systemContext.getMultiPathContext());
        finalContext.setScatteredContext(systemContext.getScatteredContext());
        finalContext.setSharedLibraryContext(systemContext.getSharedLibraryContext());
        finalContext.setTmsContext(systemContext.getTmsContext());
        finalContext.setUnusedContext(systemContext.getUnusedContext());
        finalContext.setWrongCutContext(systemContext.getWrongCutContext());

    }
}
