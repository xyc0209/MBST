package com.badsmell.analysisentity.cdabs;

import lombok.Data;
import org.joda.time.DateTime;


/**
 * author: yang
 */
@Data
public class ReceivedLog  {

    private DateTime logDateTime;
    private String logType;
    private String logIpAddr;

    private String serviceName;  //the service name who was requested
    private String logObjectId;  // the id of the MObject which writes the log
    private String logMethodName;  // the name of the function
    private String logUserId;  // the unique id for each user
    private String traceId;  //the id of the request chain
    private String id;   //the id of the current part of request
    private String parentId;  //the parent request's id of current request

    private String logFromIpAddr;
    private Integer logFromPort = 0;

    /**
     * @param record
     * @return analysis
     */
    public  boolean resolveFromString(String record){

        String[] lists = record.split("\\|",12);
        if (lists.length!=12)
            return false;

        this.setLogDateTime(DateTime.parse(lists[0]));
        this.setLogType(lists[1]);
        this.setLogIpAddr(lists[2]);
        this.setLogUserId(lists[3]);
        this.setServiceName(lists[4]);
        this.setLogObjectId(lists[5]);
        this.setLogMethodName(lists[6]);
        this.setTraceId(lists[7]);
        this.setId(lists[8]);
        this.setParentId(lists[9]);
        this.setLogFromIpAddr(lists[10]);
        this.setLogFromPort(Integer.valueOf(lists[11]));
        System.out.println(this.toString());
        return true;

    }

}
