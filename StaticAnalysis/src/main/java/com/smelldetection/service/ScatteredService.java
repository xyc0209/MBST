package com.smelldetection.service;

import com.smelldetection.base.context.ScatteredContext;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.utils.ESBParserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-14 15:56
 */
@Service
public class ScatteredService {

    @Autowired
    public ESBParserUtils esbParserUtils;

    public ScatteredContext getSFServices(RequestItem request){
        ScatteredContext scatteredContext = new ScatteredContext();
        String path = request.getServicesPath();
        Map<String, HashMap<String,Integer>> callMap =  esbParserUtils.ScatteredAnalysis(path);
        int threshold = 3;
        for(String serviceName: callMap.keySet()){
            HashMap<String,Integer> serviceMap = callMap.get(serviceName);
            for(String calledService: serviceMap.keySet()){
                // if called num > threshold, this two svcs are scattered
                if(serviceMap.get(calledService) > threshold){
                    boolean isExist = false;
                    for(Set<String> scatteredSet: scatteredContext.getScatteredList()){
                        if(scatteredSet.contains(serviceName) && !scatteredSet.contains(calledService)){
                            scatteredSet.add(calledService);
                            isExist = true;
                            break;
                        }
                        else if(!scatteredSet.contains(serviceName) && scatteredSet.contains(calledService)){
                            scatteredSet.add(serviceName);
                            isExist = true;
                            break;
                        }
                        else if(scatteredSet.contains(serviceName) && scatteredSet.contains(calledService)){
                            isExist = true;
                            break;
                        }
                    }
                    // two svcs do not exist in result list
                    if(!isExist){
                        Set<String> scatteredSet = new HashSet<>();
                        scatteredSet.add(serviceName);
                        scatteredSet.add(calledService);
                        scatteredContext.getScatteredList().add(scatteredSet);
                    }

                }
            }
        }
//        Remove duplicates，gather functionality scattered services
        ScatteredContext sc =new ScatteredContext();
        for(Set<String> set: scatteredContext.getScatteredList()){
            boolean isExist= false;
            if(sc.getScatteredList().size() == 0){
                sc.getScatteredList().add(new HashSet<>(set));
                continue;
            }
            for(Set<String> set1: sc.getScatteredList()){
                for(String svc: set){
                    if(set1.contains(svc)){
                        set1.addAll(set);
                        isExist = true;
                        break;
                    }
                }
                if(isExist)
                    break;
            }
            if(!isExist)
                sc.getScatteredList().add(new HashSet<>(set));

        }
        if(!sc.getScatteredList().isEmpty())
            sc.setStatus(true);
        return sc;


    }
}
