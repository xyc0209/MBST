package com.smelldetection.base.context;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-14 15:53
 */
@Data
public class ScatteredContext {
    public boolean status;

    public List<Set<String>> scatteredList;

    public ScatteredContext(){
        scatteredList = new ArrayList<>();
    }
}
