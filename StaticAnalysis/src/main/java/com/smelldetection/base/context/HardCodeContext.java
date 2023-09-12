package com.smelldetection.base.context;


import com.smelldetection.base.item.FileAnalysisItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class HardCodeContext {
    public boolean status;
    private Integer record = 0;
    private Map<String, List<FileAnalysisItem>> analysisResult = new HashMap();

    public HardCodeContext() {
    }

    public void add(String serviceName, FileAnalysisItem fileAnalysisResult) {
        if(!this.analysisResult.containsKey(serviceName))
                this.analysisResult.put(serviceName,new ArrayList<>());
        this.analysisResult.get(serviceName).add(fileAnalysisResult);
        this.record++;
    }



}
