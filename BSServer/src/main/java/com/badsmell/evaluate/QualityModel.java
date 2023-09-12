package com.badsmell.evaluate;

import lombok.Data;

/**
 * @description: characteristics selected from ISO/IEC 25010:2011
 * @author: xyc
 * @date: 2023-03-27 10:27
 */
@Data
public class QualityModel {
    private static  int interoperability;
    private static  int faultTolerance;
    private static  int confidentiality;
    private static  int modularity;
    private static  int reusability;
    private static  int analysability;
    private static  int modifiability;
    private static  int adaptability;
    private static  int timeBehaviour;

    public static void main(String[] args) {

    }


}
