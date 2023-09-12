package com.smelldetection.base.context;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description: store data of unversioned API
 * @author: xyc
 * @date: 2022-12-22 10:30
 */
@Data
public class ApiVersionContext {
    public boolean status;
    public Map<String, Map<String, String>> unversionedMap;
    public Map<String, Map<String, String>> missingUrlMap;
    public ApiVersionContext(){
        this.unversionedMap = new HashMap<>();
        this.missingUrlMap = new HashMap<>();
    }

    public void addUnversionedApis(String serviceName, Map<String, String> methodAndApi){
        this.unversionedMap.put(serviceName, methodAndApi);
    }

    public void addMissingUrlMap(String serviceName, Map<String, String> methodAndApi){
        this.missingUrlMap.put(serviceName, methodAndApi);
    }

}
