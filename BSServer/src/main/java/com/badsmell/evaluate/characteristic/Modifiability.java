package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 15:01
 */
public class Modifiability {
    public static final int hasApiVersion = GoodSmellFactor.hasApiVersion;
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int noHardcode = GoodSmellFactor.noHardcode;
    public static final int noServiceGreedy = GoodSmellFactor.noServiceGreedy;
    public static final int hasApiGateway = GoodSmellFactor.hasApiGateway;
    public static final int separatedDependency = GoodSmellFactor.separatedDependency;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static final int unitaryStandards = GoodSmellFactor.unitaryStandards;
    public static final int correctServicesCut = GoodSmellFactor.correctServicesCut;
    public static final int noHub = GoodSmellFactor.noHub;
    public static final int noScatteredFunctionality = GoodSmellFactor.noScatteredFunctionality;
    public static final int noMultipath = GoodSmellFactor.noMultipath;
    public static final int noESB = GoodSmellFactor.noESB;
    public static int weight;




    public static int sum(){
        int sum = hasApiVersion + noCircleDependencies + noHardcode+ noServiceGreedy + hasApiGateway + separatedDependency + separatedDatabase + appropriateSvcIntimacy +
                unitaryStandards + correctServicesCut + noHub +noScatteredFunctionality + noMultipath + noESB;
        return sum;
    }

    public static double caculateModifiability(CaculateService caculateService){
        return (hasApiVersion * caculateService.getHasApiVersionCoverage() + noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() +
                noHardcode * caculateService.getNoHardcodeCoverage() + noServiceGreedy * caculateService.getNoServiceGreedyCoverage() +
                hasApiGateway * caculateService.getHasApiGatewayCoverage() + separatedDependency * caculateService.getSeparatedDependencyCoverage() +
                separatedDatabase * caculateService.getSeparatedDatabaseCoverage() + appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() +
                unitaryStandards * caculateService.getUnitaryStandardsCoverage() + correctServicesCut * caculateService.getCorrectServicesCutCoverage() +
                noHub * caculateService.getNoHubCoverage() + noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() +
                noMultipath * caculateService.getNoMultipathCoverage() + noESB * caculateService.getNoESBCoverage()) / (double) sum();
    }

    public static double caculateModifiabilitywithoutTS(CaculateService caculateService){
        if (!caculateService.isTS()) {
            return (hasApiVersion * caculateService.getHasApiVersionCoverage() + noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() +
                    noHardcode * caculateService.getNoHardcodeCoverage() + noServiceGreedy * caculateService.getNoServiceGreedyCoverage() +
                    hasApiGateway * caculateService.getHasApiGatewayCoverage() + separatedDependency * caculateService.getSeparatedDependencyCoverage() +
                    separatedDatabase * caculateService.getSeparatedDatabaseCoverage() + appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() + correctServicesCut * caculateService.getCorrectServicesCutCoverage() +
                    noHub * caculateService.getNoHubCoverage() + noScatteredFunctionality * caculateService.getNoScatteredFunctionalityCoverage() +
                    noMultipath * caculateService.getNoMultipathCoverage() + noESB * caculateService.getNoESBCoverage()) / ((double) sum() - unitaryStandards);

        }
        else return caculateModifiability(caculateService);
    }
    public static void main(String[] args) {
        System.out.println(Modifiability.sum());
    }
}
