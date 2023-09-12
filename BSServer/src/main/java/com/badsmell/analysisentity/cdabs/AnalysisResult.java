package com.badsmell.analysisentity.cdabs;

import com.badsmell.analysisentity.cdabs.RequestCD;
import lombok.Data;

import java.util.Map;

@Data
public class AnalysisResult {

    private String userId;
    private Boolean isContainCircle;
    private Map<String, RequestCD> reqChainMap;


}
