package com.badsmell.context;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-22 21:20
 */
@Data
public class ServiceIntimacyContext {
    public Set<String> intimateServiceList;

    public ServiceIntimacyContext(){
        intimateServiceList = new HashSet<>();
    }
}
