package com.mbs.common.log;

import org.joda.time.DateTime;

public class Trace {
    public DateTime logDateTime;     // time of output log
    public MLogType logType;         // the tyoe of log
    public String logIpAddr;         // the ip of the node(container) where the service is located
    public String logUserId;         // the unique id for each user
    public String serviceName;       // the service name who is requested
    public String logObjectId;       // the id of the MObject which writes the log
    public String logMethodName;     // the name of the function
    public String traceId;           // the id of the entire request call chain
    public String id;                // the id of the current call chain
    public String parentId;          // the id of the previous call chain
    public String logFromIpAddr;     // the ip of the node(container) from which the request came
    public Integer logFromPort;      // the port to which the request is sent

    public DateTime getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(DateTime logDateTime) {
        this.logDateTime = logDateTime;
    }

    public MLogType getLogType() {
        return logType;
    }

    public void setLogType(MLogType logType) {
        this.logType = logType;
    }

    public String getLogIpAddr() {
        return logIpAddr;
    }

    public void setLogIpAddr(String logIpAddr) {
        this.logIpAddr = logIpAddr;
    }

    public String getLogUserId() {
        return logUserId;
    }

    public void setLogUserId(String logUserId) {
        this.logUserId = logUserId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLogObjectId() {
        return logObjectId;
    }

    public void setLogObjectId(String logObjectId) {
        this.logObjectId = logObjectId;
    }

    public String getLogMethodName() {
        return logMethodName;
    }

    public void setLogMethodName(String logMethodName) {
        this.logMethodName = logMethodName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLogFromIpAddr() {
        return logFromIpAddr;
    }

    public void setLogFromIpAddr(String logFromIpAddr) {
        this.logFromIpAddr = logFromIpAddr;
    }

    public Integer getLogFromPort() {
        return logFromPort;
    }

    public void setLogFromPort(Integer logFromPort) {
        this.logFromPort = logFromPort;
    }
}
