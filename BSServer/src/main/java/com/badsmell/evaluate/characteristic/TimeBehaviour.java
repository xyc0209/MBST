package com.badsmell.evaluate.characteristic;

import com.badsmell.evaluate.GoodSmellFactor;
import com.badsmell.service.CaculateService;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 15:12
 */
public class TimeBehaviour {
    public static final int noCircleDependencies = GoodSmellFactor.noCircleDependencies;
    public static final int separatedDatabase = GoodSmellFactor.separatedDatabase;
    public static final int appropriateSvcIntimacy = GoodSmellFactor.appropriateSvcIntimacy;
    public static final int noESB = GoodSmellFactor.noESB;
    public static int weight;

    public static int sum(){
        int sum = noCircleDependencies + separatedDatabase + appropriateSvcIntimacy + noESB;
        return sum;
    }

    public static double caculateTimeBehaviour(CaculateService caculateService){
        return (noCircleDependencies * caculateService.getNoCircleDependenciesCoverage() + separatedDatabase * caculateService.getSeparatedDatabaseCoverage()+
                appropriateSvcIntimacy * caculateService.getAppropriateSvcIntimacyCoverage() + noESB * caculateService.getNoESBCoverage())/ (double) sum();
    }

    public static void main(String[] args) {
        System.out.println(TimeBehaviour.sum());
    }

}
