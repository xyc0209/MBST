package com.badsmell.service;

import com.badsmell.analysisentity.cdabs.AnalysisResult;
import com.badsmell.context.AvailabilityContext;
import com.badsmell.context.UnevenIfcContext;
import com.badsmell.context.UnevenResContext;
import com.badsmell.context.UnevenSvcContext;
import com.badsmell.context.CircleDependencyContext;
import com.badsmell.context.FinalContext;
import com.badsmell.evaluate.characteristic.*;
import com.smelldetection.base.context.*;
import com.smelldetection.base.item.MultiPathItem;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 20:26
 */
@Service
@Data
public class CaculateService {
    public static int servicesCount;
    public static  Set<String> serviceList;
    public static double qualityScore;
    public static Map<String, Double> qualityScoreDetail;
    public static double runtimeQualityScore;
    private double hasApiVersionCoverage;
    private double noCircleReferenceCoverage;
    private double noHardcodeCoverage;
    private double noServiceGreedyCoverage;
    private double hasApiGatewayCoverage;
    private double separatedDependencyCoverage;
    private double unitaryStandardsCoverage;
    private double correctServicesCutCoverage;
    private double noHubCoverage;
    private double noScatteredFunctionalityCoverage;
    private double noMultipathCoverage;
    private double fullUsedAbstractCoverage;
    private double fullUsedInterfaceCoverage;
    private double noESBCoverage;
    private double separatedDatabaseCoverage;
    private double appropriateSvcIntimacyCoverage;
    public Set<String> sharedDatabaseSet;
    public Set<String> serviceIntimacySet;
    private double noCircleDependenciesCoverage;
    @Autowired
    private FinalContext finalContext;

    public void processSystemContext(SystemContext systemContext){
        servicesCount = systemContext.getServicesCount();
        serviceList = systemContext.getServiceContext().getServiceList();
        processApiVersionContext(systemContext.getApiVersionContext());
        processCyclicReferenceContext(systemContext.getCyclicReferenceContext());
        processHardCodeContext(systemContext.getHardCodeContext());
        processGreedyContext(systemContext.getGreedyContext());
        processGateWayContext(systemContext.getGateWayContext());
        processSharedLibraryContext(systemContext.getSharedLibraryContext());
        processTMSContext(systemContext.getTmsContext());
        processWrongCutContext(systemContext.getWrongCutContext());
        processHubContext(systemContext.getHubContext());
        processScatteredContext(systemContext.getScatteredContext());
        processMultiPathContext(systemContext.getMultiPathContext());
        processUnusedContext(systemContext.getUnusedContext());
        processESBServiceContext(systemContext.getEsbServiceContext());
        processSharedDatabaseContext(systemContext.getSharedDatabaseContext());

        System.out.println("fullUsedAbstractCoverage:    " + fullUsedAbstractCoverage);
        System.out.println("fullUsedInterfaceCoverage:    " + fullUsedInterfaceCoverage);
    }
    public void processApiVersionContext(ApiVersionContext apiVersionContext){
        if(apiVersionContext.status == false){
            hasApiVersionCoverage = 1;
            return;
        }
        int unVersionedCount = 0;
        for(String s: apiVersionContext.getUnversionedMap().keySet()){
            if(!apiVersionContext.getUnversionedMap().get(s).isEmpty()){
                unVersionedCount++;
            }
        }
        hasApiVersionCoverage = 1 - unVersionedCount/(double)servicesCount;
    }
    public void processCyclicReferenceContext(CyclicReferenceContext cyclicReferenceContext){
        Set<String> services = new HashSet<>();
        for(String cla: cyclicReferenceContext.getCyclicReference().keySet()){
            services.add(cyclicReferenceContext.getCyclicReference().get(cla).getServiceName());
        }
        noCircleReferenceCoverage = 1 - services.size()/(double)servicesCount;
    }
    public void processHardCodeContext(HardCodeContext hardCodeContext){
        noHardcodeCoverage = 1 - hardCodeContext.getAnalysisResult().size()/(double)servicesCount;
    }
    public void processGreedyContext(GreedyContext greedyContext){
        noServiceGreedyCoverage = 1 - greedyContext.getGreedySvc().size()/(double)servicesCount;
    }
    public void processGateWayContext(GateWayContext gateWayContext){
        hasApiGatewayCoverage = gateWayContext.isHasGateWay() ? 1 :0;
    }
    public void processSharedLibraryContext(SharedLibraryContext sharedLibraryContext){
        Set<String> serviceList = new HashSet<>();
        for(String library: sharedLibraryContext.getSharedLibraries().keySet())
            for(String service: sharedLibraryContext.getSharedLibraries().get(library).getSharedServices())
                serviceList.add(service);
        separatedDependencyCoverage = 1 - serviceList.size() / (double)servicesCount;
    }
    public void processTMSContext(TMSContext tmsContext){
        if(!tmsContext.getValueMap().getIsMussy())
            unitaryStandardsCoverage = 1.0;
        else {
            double javaCount = 0.0;
            double sum = 0.0;
            for(String language: tmsContext.getValueMap().getProgramingLang().keySet()) {
                if (tmsContext.getValueMap().getProgramingLang().get(language).equals("Java"))
                    javaCount = tmsContext.getValueMap().getProgramingLang().get(language).getBytes();
                sum += tmsContext.getValueMap().getProgramingLang().get(language).getBytes();
            }
            unitaryStandardsCoverage = javaCount/sum;

        }

    }
    public void processWrongCutContext(WrongCutContext wrongCutContext){
        correctServicesCutCoverage = 1- wrongCutContext.getWrongCutMap().size() / (double)servicesCount;
    }
    public void processHubContext(HubContext hubContext){
        noHubCoverage = 1- hubContext.getHubclass().size() / (double)servicesCount;
    }
    public void processScatteredContext(ScatteredContext scatteredContext){
        Set<String> svc = new HashSet<>();
        for(Set<String> scatteredSet: scatteredContext.getScatteredList())
            for(String service: scatteredSet)
                svc.add(service);
        noScatteredFunctionalityCoverage = 1 - svc.size() / (double)servicesCount;

    }
    public void processMultiPathContext(MultiPathContext multiPathContext){
        Set<String> service = new HashSet<>();
        for(MultiPathItem multiPathItem: multiPathContext.getMultiItems()){
            service.add(multiPathItem.getServiceName());
        }
        noMultipathCoverage = 1 - service.size() / (double)servicesCount;
    }
    public void processUnusedContext(UnusedContext unusedContext){
        int abstractCount = unusedContext.getAbstractCount();
        int interfaceCount = unusedContext.getInterfaceCount();
        List<String> unusedAbstract = new ArrayList<>();
        List<String> unusedInterface = new ArrayList<>();
        for(String service: unusedContext.getNotFullyUsedAbstract().keySet())
            for(String abst: unusedContext.getNotFullyUsedAbstract().get(service))
                unusedAbstract.add(abst);
        for(String service: unusedContext.getNotFullyUsedInterface().keySet())
            for(String ifc: unusedContext.getNotFullyUsedInterface().get(service))
                unusedInterface.add(ifc);
        if(abstractCount != 0)
            fullUsedAbstractCoverage = 1 - unusedAbstract.size() / (double)abstractCount;
        else
            fullUsedAbstractCoverage = 1;
        if(interfaceCount != 0)
            fullUsedInterfaceCoverage = 1 - unusedInterface.size() / (double)interfaceCount;
        else
            fullUsedInterfaceCoverage = 1;
        System.out.println("fullUsedAbstractCoverage" +fullUsedAbstractCoverage);
        System.out.println("fullUsedInterfaceCoverage"+fullUsedInterfaceCoverage);
    }
    public void processESBServiceContext(ESBServiceContext esbServiceContext){
        int esbCount = 0;
        for(String service: esbServiceContext.getResult().keySet())
            if(esbServiceContext.getResult().get(service).getIsESBUsage())
                esbCount++;
        noESBCoverage = 1 - esbCount/ (double)servicesCount;
    }
    public void processSharedDatabaseContext(SharedDatabaseContext sharedDatabaseContext){
        for(String database: sharedDatabaseContext.getSharedDatabaseMap().keySet())
            for(String service: sharedDatabaseContext.getSharedDatabaseMap().get(database))
                sharedDatabaseSet.add(service);
        for(String database: sharedDatabaseContext.getServiceIntimacyMap().keySet())
            for(String service: sharedDatabaseContext.getServiceIntimacyMap().get(database))
                serviceIntimacySet.add(service);
    }

    public void processCircleDependencies(CircleDependencyContext circleDependencyContext){
        finalContext.setCircleDependencyContext(circleDependencyContext);
        Set<String> cycleDependencyService = new HashSet<>();
        for(AnalysisResult analysisResult: circleDependencyContext.getValueMap()){
            if(analysisResult.getIsContainCircle()){
                for(String traceId: analysisResult.getReqChainMap().keySet()){
                    if(analysisResult.getReqChainMap().get(traceId).getIsCircle()){
                        String serviceName = analysisResult.getReqChainMap().get(traceId).getTreeRoot().getServiceName();
                        if(serviceList.contains(serviceName))
                            cycleDependencyService.add(serviceName);

                    }
                }
            }
        }
        noCircleDependenciesCoverage = 1 - cycleDependencyService.size() / (double) servicesCount;

    }

    public void judgeSharedDatabase(){
        separatedDatabaseCoverage = 1 - sharedDatabaseSet.size() / (double) servicesCount;
    }

    public void judgeServiceIntimacy(){
        appropriateSvcIntimacyCoverage = 1 - serviceIntimacySet.size() / (double) servicesCount;
    }

    public void caculateQualityScore(){
        int weightSum = setWeight(1, 1, 1, 1, 1, 1, 1, 1, 1);
        System.out.println("weightSum"+weightSum);
        System.out.println("Adaptability.weight * Adaptability.caculateAdaptability(this)"+Adaptability.weight * Adaptability.caculateAdaptability(this));
        System.out.println("Analysability.weight * Analysability.caculateAnalysability(this)"+Analysability.weight * Analysability.caculateAnalysability(this));
        System.out.println("Confidentiality.weight * Confidentiality.caculateConfidentiality(this)"+Confidentiality.weight * Confidentiality.caculateConfidentiality(this));
        System.out.println("FaultTolerance.weight * FaultTolerance.caculateFaultTolerance(this)"+FaultTolerance.weight * FaultTolerance.caculateFaultTolerance(this));
        System.out.println("Interoperability.weight * Interoperability.caculateInteroperability(this)"+Interoperability.weight * Interoperability.caculateInteroperability(this));
        System.out.println("Modifiability.weight  * Modifiability.caculateModifiability(this)"+Modifiability.weight  * Modifiability.caculateModifiability(this));
        System.out.println("Modularity.weight * Modularity.caculateModularity(this)"+Modularity.weight * Modularity.caculateModularity(this));
        System.out.println("Reusability.weight * Reusability.caculateReusability(this)"+Reusability.weight * Reusability.caculateReusability(this));
        System.out.println("TimeBehaviour.weight * TimeBehaviour.caculateTimeBehaviour(this)"+TimeBehaviour.weight * TimeBehaviour.caculateTimeBehaviour(this));
        qualityScoreDetail = new HashMap<>();
        qualityScoreDetail.put("Adaptability",Adaptability.weight * Adaptability.caculateAdaptability(this));
        qualityScoreDetail.put("Analysability", Analysability.weight * Analysability.caculateAnalysability(this));
        qualityScoreDetail.put("Confidentiality",Confidentiality.weight * Confidentiality.caculateConfidentiality(this));
        qualityScoreDetail.put("FaultTolerance",FaultTolerance.weight * FaultTolerance.caculateFaultTolerance(this));
        qualityScoreDetail.put("Interoperability",Interoperability.weight * Interoperability.caculateInteroperability(this));
        qualityScoreDetail.put("Modifiability", Modifiability.weight  * Modifiability.caculateModifiability(this));
        qualityScoreDetail.put("Modularity", Modularity.weight * Modularity.caculateModularity(this));
        qualityScoreDetail.put("Reusability",Reusability.weight * Reusability.caculateReusability(this));
        qualityScoreDetail.put("TimeBehaviour",TimeBehaviour.weight * TimeBehaviour.caculateTimeBehaviour(this));
        qualityScore  = (Adaptability.weight * Adaptability.caculateAdaptability(this) +  Analysability.weight * Analysability.caculateAnalysability(this) +
                Confidentiality.weight * Confidentiality.caculateConfidentiality(this) + FaultTolerance.weight * FaultTolerance.caculateFaultTolerance(this) +
                Interoperability.weight * Interoperability.caculateInteroperability(this) + Modifiability.weight  * Modifiability.caculateModifiability(this) +
                Modularity.weight * Modularity.caculateModularity(this) + Reusability.weight * Reusability.caculateReusability(this) +
                TimeBehaviour.weight * TimeBehaviour.caculateTimeBehaviour(this)) * 100 / (double) weightSum;

    }
    public int setWeight(int adaptabilityWeight, int analysabilityWeight, int confidentialityWeight, int FaultToleranceWeight, int interoperabilityWeight, int modifiabilityWeight,
                     int modularityWeight, int reusabilityWeight, int timeBehaviourWeight){
        Adaptability.weight = adaptabilityWeight;
        Analysability.weight = analysabilityWeight;
        Confidentiality.weight = confidentialityWeight;
        FaultTolerance.weight = FaultToleranceWeight;
        Interoperability.weight = interoperabilityWeight;
        Modifiability.weight = modifiabilityWeight;
        Modularity.weight = modularityWeight;
        Reusability.weight = reusabilityWeight;
        TimeBehaviour.weight = timeBehaviourWeight;
        return (adaptabilityWeight + analysabilityWeight + confidentialityWeight + FaultToleranceWeight + interoperabilityWeight + modifiabilityWeight + modularityWeight + reusabilityWeight
                + timeBehaviourWeight);
    }

    public void caculateRuntimeScore(AvailabilityContext availabilityContext, UnevenSvcContext unevenSvcContext, UnevenIfcContext unevenIfcContext, UnevenResContext unevenResContext){
        runtimeQualityScore =  (availabilityContext.getSoh() + unevenSvcContext.getSoh() + unevenIfcContext.getSoh() +
                (unevenResContext.getCpuSoh() +unevenResContext.getRamSoh()) / 2) * 100 / 4;
    }


}
