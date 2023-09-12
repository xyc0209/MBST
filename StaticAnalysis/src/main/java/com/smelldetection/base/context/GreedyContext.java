package com.smelldetection.base.context;

import com.smelldetection.base.item.ServiceItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-13 15:16
 */
@Data
public class GreedyContext {
    public boolean status;

    public List<ServiceItem> greedySvc;

    public GreedyContext(){
        this.greedySvc = new ArrayList<>();
    }
    public void addGreedySvc(ServiceItem serviceItem){
        this.getGreedySvc().add(serviceItem);
    }
}
