package com.smelldetection.base.context;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: xyc
 * @date: 2023-01-10 22:16
 */
@Data
public class UnusedContext {
    public boolean status;
    public int abstractCount;
    public int interfaceCount;
    public Map<String, Set<String>> notFullyUsedInterface;
    public Map<String, Set<String>> notFullyUsedAbstract;
    public UnusedContext(){
        this.notFullyUsedInterface = new HashMap<>();
        this.notFullyUsedAbstract = new HashMap<>();
    }
}
