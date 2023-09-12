package com.badsmell.context;

import com.badsmell.analysisentity.cdabs.AnalysisResult;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-30 11:21
 */
@Data
public class CircleDependencyContext {
    public boolean status;
    private List<AnalysisResult> valueMap = new ArrayList<>();
}
