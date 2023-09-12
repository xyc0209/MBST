package com.badsmell.service;

import com.badsmell.context.ServiceIntimacyContext;
import com.mbs.common.log.DatabaseLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-22 17:29
 */
@Service
public class ServiceIntimacyService {
    @Autowired
    public CaculateService caculateService;


    public ServiceIntimacyContext judgeIntimateServices(List<String> logRecords) {
        ServiceIntimacyContext serviceIntimacyContext = new ServiceIntimacyContext();
        List<DatabaseLog> databaseLogList = new ArrayList<>();
        if (logRecords != null && logRecords.size() != 0) {
            for(int i = 0; i < logRecords.size(); ++i) {
                DatabaseLog databaseLog = new DatabaseLog();
                Boolean success = databaseLog.resolveFromString((String) logRecords.get(i));
                System.out.println(success);
                if (success) {
                    databaseLogList.add(databaseLog);
                }
                else {
                    System.out.println("Log parse falied!!");
                }
            }
        }
        for(int i = 0; i < databaseLogList.size() - 1; i++) {
            for(int j= i+1; j < databaseLogList.size(); j++){
                if(databaseLogList.get(i).getTable().equals(databaseLogList.get(j).getTable()) && !databaseLogList.get(i).getServiceName().equals(databaseLogList.get(j).getServiceName()))
                       if(!databaseLogList.get(i).getSecondaryDatabaseUrl().equals("null") && !databaseLogList.get(i).getOperate().name().equals("select"))
                            serviceIntimacyContext.getIntimateServiceList().add(databaseLogList.get(i).getServiceName());
                       else if (!databaseLogList.get(j).getSecondaryDatabaseUrl().equals("null") && !databaseLogList.get(j).getOperate().name().equals("select")) {
                           serviceIntimacyContext.getIntimateServiceList().add(databaseLogList.get(j).getServiceName());
                       }
            }
        }
        Set<String> intimacyService = new HashSet<>();
        for(String service: serviceIntimacyContext.getIntimateServiceList()){
            if(caculateService.getServiceIntimacySet().contains(service))
                intimacyService.add(service);
        }
        caculateService.setServiceIntimacySet(intimacyService);
        return serviceIntimacyContext;

    }
}
