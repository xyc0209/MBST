package com.mbs.common.log;

import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class DatabaseLog {
    public long currentTimeMillis;           // time of output log
    public String framework;                 // data access framework
    public String serviceName;               // service name
    public String databaseUrl;               // database url for intimate service
    public String databaseName;              // database name for intimate service
    public String secondaryDatabaseUrl;      // database url for forced intimate service
    public String secondaryDatabaseName;     // database name for forced intimate service
    public String table;                     // table name accessed
    public Operate operate;                  // data operation

    public String toString() {
        return  String.valueOf(currentTimeMillis) + '|'+
                framework + '|' +
                serviceName + '|' +
                databaseUrl + '|' +
                databaseName + '|' +
                secondaryDatabaseUrl + '|' +
                secondaryDatabaseName + '|' +
                table + '|' +
                operate.name();
    }
    public JSONObject toJson() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("currentTimeMillis", currentTimeMillis);
        jsonMap.put("framework", framework);
        jsonMap.put("serviceName", serviceName);
        jsonMap.put("databaseUrl", databaseUrl);
        jsonMap.put("databaseName", databaseName);
        jsonMap.put("table", table);
        jsonMap.put("operate", operate.name());
        return new JSONObject(jsonMap);
    }
    public JSONObject twoSourcesToJson() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("currentTimeMillis", currentTimeMillis);
        jsonMap.put("framework", framework);
        jsonMap.put("serviceName", serviceName);
        jsonMap.put("databaseUrl", databaseUrl);
        jsonMap.put("databaseName", databaseName);
        jsonMap.put("secondaryDatabaseUrl", secondaryDatabaseUrl);
        jsonMap.put("secondaryDatabaseName", secondaryDatabaseName);
        jsonMap.put("table", table);
        jsonMap.put("operate", operate.name());
        return new JSONObject(jsonMap);
    }

  public boolean resolveFromString(String record) {
        String[] lists = record.split("\\|", 9);
        if (lists.length != 9) {
            return false;
        } else {
            this.setCurrentTimeMillis(Long.parseLong(lists[0]));
            this.setFramework(lists[1]);
            this.setServiceName(lists[2]);
            this.setDatabaseUrl(lists[3]);
            this.setDatabaseName(lists[4]);
            this.setSecondaryDatabaseUrl(lists[5]);
            this.setSecondaryDatabaseName(lists[6]);
            this.setTable(lists[7]);
            this.setOperate(Operate.valueOf(lists[8]));
            return true;
        }
    }

    public static DatabaseLog getLogFromMap(Map<String, Object> logMap) {
        DatabaseLog databaseLog = new DatabaseLog();
        databaseLog.currentTimeMillis = (long) logMap.get("currentTimeMillis");
        databaseLog.framework = (String) logMap.get("framework");
        databaseLog.serviceName = (String) logMap.get("serviceName");
        databaseLog.databaseUrl = (String) logMap.get("databaseUrl");
        databaseLog.databaseName = (String) logMap.get("databaseName");
        databaseLog.secondaryDatabaseUrl = (String) logMap.get("secondaryDatabaseUrl");
        databaseLog.secondaryDatabaseName = (String) logMap.get("secondaryDatabaseName");
        databaseLog.table = (String) logMap.get("table");
        databaseLog.operate = Operate.valueOf((String)logMap.get("operate"));
        return  databaseLog;
    }
}
