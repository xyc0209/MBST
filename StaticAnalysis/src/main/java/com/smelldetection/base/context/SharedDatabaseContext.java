package com.smelldetection.base.context;

import lombok.Data;

import java.util.*;

@Data
public class SharedDatabaseContext {


    public Map<String, ArrayList<String>> sharedDatabaseMap;
    public Map<String, ArrayList<String>> serviceIntimacyMap;


    public SharedDatabaseContext() {
        this.sharedDatabaseMap = new HashMap<>();
        this.serviceIntimacyMap = new HashMap<>();
    }

    public void addSharedDatabase(String databaseName, ArrayList<String> list){
        this.sharedDatabaseMap.put(databaseName,list);
    }

    public void addServiceIntimacy(String databaseName,ArrayList<String> serviceIntimacySet){
        this.serviceIntimacyMap.put(databaseName,serviceIntimacySet);
    }

}
