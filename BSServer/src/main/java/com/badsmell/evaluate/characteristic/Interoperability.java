package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 11:05
 */
public class Interoperability {
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int noServiceGreedy = GoodSmellFactor.noServiceGreedy;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int unitaryStandards = GoodSmellFactor.unitaryStandards;
    public static final int noScatteredFunctionality = GoodSmellFactor.noScatteredFunctionality;
    public static final int noESB = GoodSmellFactor.noESB;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static int weight;

    public static int sum(){
        int sum = noCircleDependencies + noServiceGreedy + separatedDatabase + unitaryStandards + noScatteredFunctionality + noESB + appropriateSvcIntimacy;
        return sum;
    }

    public static double caculateInteroperability(CaculateService caculateService){
        return ( noServiceGreedy * caculateService.getNoServiceGreedyCoverage() + separatedDatabase * caculateService.getSeparatedDatabaseCoverage() +
                appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() + unitaryStandards * caculateService.getUnitaryStandardsCoverage() +
                noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() + noESB * caculateService.getNoESBCoverage() -
                noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() ) / (double) sum();

    }
    public static void main(String[] args) {
        System.out.println(Interoperability.sum());
    }

}
