package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

import java.sql.SQLOutput;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 15:09
 */
public class Adaptability {
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int noHardcode = GoodSmellFactor.noHardcode;
    public static final int noServiceGreedy = GoodSmellFactor.noServiceGreedy;
    public static final int correctServicesCut = GoodSmellFactor.correctServicesCut;
    public static final int noScatteredFunctionality = GoodSmellFactor.noScatteredFunctionality;
    public static final int noESB = GoodSmellFactor.noESB;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static final int fullUsedAbstract = GoodSmellFactor.fullUsedAbstract;
    public static final int fullUsedInterface = GoodSmellFactor.fullUsedInterface;
    public static int weight;




    public static int sum(){
        int sum = noCircleDependencies + noHardcode+ noServiceGreedy + + correctServicesCut + noScatteredFunctionality + noESB + separatedDatabase + appropriateSvcIntimacy + fullUsedAbstract + fullUsedInterface;
        return sum;
    }

    public static double caculateAdaptability(CaculateService caculateService){

        return (noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() + noHardcode * caculateService.getNoHardcodeCoverage() +
                noServiceGreedy * caculateService.getNoServiceGreedyCoverage() + correctServicesCut * caculateService.getCorrectServicesCutCoverage() +
                noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() + noESB * caculateService.getNoESBCoverage() +
                separatedDatabase * caculateService.getSeparatedDatabaseCoverage() + appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() +
                fullUsedAbstract * caculateService.getFullUsedAbstractCoverage() + fullUsedInterface * caculateService.getFullUsedInterfaceCoverage()) / (double) sum();

    }

    public static void main(String[] args) {
        System.out.println(Adaptability.sum());
    }

}
