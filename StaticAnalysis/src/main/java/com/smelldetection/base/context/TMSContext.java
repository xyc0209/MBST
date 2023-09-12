package com.smelldetection.base.context;

import com.smelldetection.base.item.TMSAnalysisResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-02-26 20:40
 */
@Data
public class TMSContext {
    public boolean status;
    public Map<String, Double> languageCount;
    public Map<String, List<String>> fileList;
    public TMSAnalysisResult valueMap;
//    public TMSContext(){
//        this.languageCount = new HashMap<>();
//        this.fileList = new HashMap<>();
//        this.valueMap = new TMSAnalysisResult();
//    }
}
