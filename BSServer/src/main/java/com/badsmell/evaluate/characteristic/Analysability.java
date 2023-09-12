package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 11:32
 */
public class Analysability {
    public static final int hasApiVersion = GoodSmellFactor.hasApiVersion;
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int noHardcode = GoodSmellFactor.noHardcode;
    public static final int noServiceGreedy = GoodSmellFactor.noServiceGreedy;
    public static final int hasApiGateway = GoodSmellFactor.hasApiGateway;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static final int unitaryStandards = GoodSmellFactor.unitaryStandards;
    public static final int correctServicesCut = GoodSmellFactor.correctServicesCut;
    public static final int noHub = GoodSmellFactor.noHub;
    public static final int noCircleReference = GoodSmellFactor.noCircleReference;
    public static final int noScatteredFunctionality = GoodSmellFactor.noScatteredFunctionality;
    public static final int noMultipath = GoodSmellFactor.noMultipath;
    public static final int fullUsedAbstract = GoodSmellFactor.fullUsedAbstract;
    public static final int fullUsedInterface = GoodSmellFactor.fullUsedInterface;
    public static final int noESB = GoodSmellFactor.noESB;
    public static int weight;




    public static int sum(){
        int sum = hasApiVersion + noCircleDependencies + noHardcode + noServiceGreedy + hasApiGateway + separatedDatabase + appropriateSvcIntimacy +
                unitaryStandards + correctServicesCut + noHub + noCircleReference + noScatteredFunctionality + noMultipath + fullUsedAbstract +
                fullUsedInterface + noESB;
        return sum;
    }

    public static double caculateAnalysability(CaculateService caculateService){
        return (hasApiVersion * caculateService.getHasApiVersionCoverage() + noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() +
                noHardcode * caculateService.getNoHardcodeCoverage() + noServiceGreedy * caculateService.getNoServiceGreedyCoverage() +
                hasApiGateway * caculateService.getHasApiGatewayCoverage() + separatedDatabase * caculateService.getSeparatedDatabaseCoverage() +
                appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() + unitaryStandards * caculateService.getUnitaryStandardsCoverage() +
                correctServicesCut * caculateService.getCorrectServicesCutCoverage() + noHub * caculateService.getNoHubCoverage() +
                noCircleReference * caculateService.getNoCircleReferenceCoverage() + noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() +
                noMultipath * caculateService.getNoMultipathCoverage() + fullUsedAbstract * caculateService.getFullUsedAbstractCoverage() +
                fullUsedInterface * caculateService.getFullUsedInterfaceCoverage() + noESB * caculateService.getNoESBCoverage()) / (double) sum();
    }

    public static void main(String[] args) {
        System.out.println(Analysability.sum());
    }
}
