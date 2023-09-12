package com.smelldetection.base.context;

import com.smelldetection.base.item.DependCount;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-13 17:32
 */
@Data
public class HubContext {
    public boolean status;
    public int systemClassCount;
    public Map<String, DependCount> hubclass;

    public HubContext(){
        hubclass = new HashMap<>();
    }

    public void addHubclass(String serviceName, DependCount dependCount){
        this.getHubclass().put(serviceName, dependCount);
    }
}
