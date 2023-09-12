package com.badsmell.hardcodingabs;

import com.badsmell.hardcodingabs.FileAnalysisResult;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HCAnalysisResult {

    private Integer record;  //record the number of hardcoding

    private Map<Integer, FileAnalysisResult> analysisResult;

    public HCAnalysisResult(){
        record = 0;
        analysisResult = new HashMap<>();
    }

    public void add(FileAnalysisResult fileAnalysisResult){
        record++;
        analysisResult.put(record,fileAnalysisResult);
    }
}
