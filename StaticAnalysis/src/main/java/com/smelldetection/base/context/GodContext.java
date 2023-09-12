package com.smelldetection.base.context;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-27 19:16
 */
@Data
public class GodContext {
    public boolean status;

    public Map<String, Map<String,Integer>> godServiceMap;

    public GodContext(){
        this.godServiceMap = new HashMap<>();
    }
}
