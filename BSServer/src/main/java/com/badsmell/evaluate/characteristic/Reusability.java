package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 11:29
 */
public class Reusability {
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int noHardcode = GoodSmellFactor.noHardcode;
    public static final int noServiceGreedy = GoodSmellFactor.noServiceGreedy;
    public static final int correctServicesCut = GoodSmellFactor.correctServicesCut;
    public static final int noScatteredFunctionality = GoodSmellFactor.noScatteredFunctionality;
    public static final int noESB = GoodSmellFactor.noESB;
    public static final int fullUsedAbstract = GoodSmellFactor.fullUsedAbstract;
    public static final int fullUsedInterface = GoodSmellFactor.fullUsedInterface;
    public static int weight;



    public static int sum(){
        int sum = noCircleDependencies + noHardcode + noServiceGreedy + correctServicesCut + noScatteredFunctionality + noESB + fullUsedAbstract + fullUsedInterface;
        return sum;
    }

    public static double caculateReusability(CaculateService caculateService){
        System.out.println("caculateService.getNoCircleDependenciesCoverage()"+caculateService.getNoCircleDependenciesCoverage());
        System.out.println("caculateService.getNoHardcodeCoverage()"+caculateService.getNoHardcodeCoverage());
        System.out.println("caculateService.getNoServiceGreedyCoverage()"+caculateService.getNoServiceGreedyCoverage());
        System.out.println("caculateService.getCorrectServicesCutCoverage()"+caculateService.getCorrectServicesCutCoverage());
        System.out.println("caculateService.getNoScatteredFunctionalityCoverage()"+caculateService.getNoScatteredFunctionalityCoverage());
        System.out.println("caculateService.getNoESBCoverage()"+caculateService.getNoESBCoverage());
        System.out.println("caculateService.getFullUsedAbstractCoverage()"+caculateService.getFullUsedAbstractCoverage());
        System.out.println("caculateService.getFullUsedInterfaceCoverage()"+caculateService.getFullUsedInterfaceCoverage());
        return (noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() + noHardcode * caculateService.getNoHardcodeCoverage() +
                noServiceGreedy * caculateService.getNoServiceGreedyCoverage() + correctServicesCut * caculateService.getCorrectServicesCutCoverage() +
                noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() + noESB * caculateService.getNoESBCoverage() +
                fullUsedAbstract * caculateService.getFullUsedAbstractCoverage() + fullUsedInterface * caculateService.getFullUsedInterfaceCoverage()) / (double) sum();
    }


    public static void main(String[] args) {
        System.out.println(Reusability.sum());
    }
}
