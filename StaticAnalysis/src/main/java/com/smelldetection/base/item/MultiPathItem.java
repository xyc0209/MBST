package com.smelldetection.base.item;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-02-24 15:37
 */
@Data
public class MultiPathItem {
    public String name;
    public String serviceName;
    public List<List<String>> routeList;

}
