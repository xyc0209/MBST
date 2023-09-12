package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 11:14
 */
public class FaultTolerance {
    public static final int hasApiVersion = GoodSmellFactor.hasApiVersion;
    public static final int hasApiGateway = GoodSmellFactor.hasApiGateway;
    public static final int separatedDependency = GoodSmellFactor.separatedDependency;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static final int noCircleReference = GoodSmellFactor.noCircleReference;
    public static final int noESB = GoodSmellFactor.noESB;
    public static int weight;



    public static int sum(){
        int sum = hasApiVersion + hasApiGateway + separatedDependency + separatedDatabase + appropriateSvcIntimacy + noCircleReference + noESB;
        return sum;
    }

    public static double caculateFaultTolerance(CaculateService caculateService){
        return (hasApiVersion * caculateService.getHasApiVersionCoverage() - hasApiGateway * caculateService.getHasApiGatewayCoverage() +
                separatedDependency * caculateService.getSeparatedDependencyCoverage() + separatedDatabase * caculateService.getSeparatedDatabaseCoverage() +
                appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() + noCircleReference * caculateService.getNoCircleReferenceCoverage() +
                noESB * caculateService.getNoESBCoverage()) / (double) sum();
    }

    public static void main(String[] args) {
        System.out.println(FaultTolerance.sum());
    }
}
