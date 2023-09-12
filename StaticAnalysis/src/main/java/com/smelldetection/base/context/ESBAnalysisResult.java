package com.smelldetection.base.context;

import lombok.Data;

import java.util.HashMap;

@Data
public class ESBAnalysisResult {
    private HashMap<String,Boolean> serviceSet;
}
