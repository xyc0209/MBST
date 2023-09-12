package com.badsmell.analysisentity.cdabs;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CircleDescribeTree {
    private String id;
    private String parentId;
    private String serviceName;
    private String serviceInterface;
    private String instanceIpAddr;
    private Map<String,CircleDescribeTree> subRequest;


    public CircleDescribeTree(){
        this.subRequest = new HashMap<>();
    }
}
