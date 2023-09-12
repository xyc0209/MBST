package com.smelldetection.base.context;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-26 21:25
 */
@Data
public class WrongCutContext {
    public boolean status;

    public Map<String,Map<String,Integer>> wrongCutMap;

    public WrongCutContext(){
        this.wrongCutMap = new HashMap<>();
    }
}
