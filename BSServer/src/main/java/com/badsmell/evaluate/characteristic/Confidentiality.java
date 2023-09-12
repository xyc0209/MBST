package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 11:20
 */
public class Confidentiality {
    public static final int noHardcode = GoodSmellFactor.noHardcode;
    public static final int hasApiGateway = GoodSmellFactor.hasApiGateway;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static int weight;



    public static int sum(){
        int sum = noHardcode + hasApiGateway + separatedDatabase + appropriateSvcIntimacy;
        return sum;
    }
    public static double caculateConfidentiality(CaculateService caculateService){
        return (noHardcode * caculateService.getNoHardcodeCoverage() + hasApiGateway * caculateService.getHasApiGatewayCoverage() +
                separatedDatabase * caculateService.getSeparatedDatabaseCoverage() + appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage()) / (double) sum();

    }
    public static void main(String[] args) {
        System.out.println(Confidentiality.sum());
    }
}
