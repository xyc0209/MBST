package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 11:23
 */
public class Modularity {
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int noServiceGreedy = GoodSmellFactor.noServiceGreedy;
    public static final int separatedDependency = GoodSmellFactor.separatedDependency;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static final int correctServicesCut = GoodSmellFactor.correctServicesCut;
    public static final int noScatteredFunctionality = GoodSmellFactor.noScatteredFunctionality;
    public static final int noESB = GoodSmellFactor.noESB;
    public static int weight;



    public static int sum(){
        int sum = noCircleDependencies + noServiceGreedy + separatedDependency + separatedDatabase + appropriateSvcIntimacy + correctServicesCut +
                + noScatteredFunctionality+ noESB;
        return sum;
    }

    public static double caculateModularity(CaculateService caculateService){
        return (noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() + noServiceGreedy * caculateService.getNoServiceGreedyCoverage() +
                separatedDependency * caculateService.getSeparatedDependencyCoverage() + separatedDatabase * caculateService.getSeparatedDatabaseCoverage() +
                appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() + correctServicesCut * caculateService.getCorrectServicesCutCoverage() +
                noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() + noESB * caculateService.getNoESBCoverage()) / (double) sum();
    }

    public static void main(String[] args) {
        System.out.println(Modularity.sum());
    }
}
