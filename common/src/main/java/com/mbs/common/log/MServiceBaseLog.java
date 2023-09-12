package com.mbs.common.log;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Map;


@Getter
@Setter
public abstract class MServiceBaseLog extends MBaseLog {
    protected String serviceName;  //the service name who is requested
    protected String logObjectId;  // the id of the MObject which writes the log
    protected String logMethodName;  // the name of the function
    protected String logUserId;  // the unique id for each user

    @Override
    protected String[] fillInfo(String[] strArr) {
        String[] leftStrArr = super.fillInfo(strArr);
        if (leftStrArr != null) {
            this.logUserId = leftStrArr[0];
            this.serviceName = leftStrArr[1];
            this.logObjectId = leftStrArr[2];
            this.logMethodName = leftStrArr[3];
            return getUnusedStrArr(leftStrArr, 4);
        }
        return null;
    }

    @Override
    protected void fillInfo(Map<String, Object> logMap) {
        super.fillInfo(logMap);
        this.logUserId = (String)logMap.get("logUserId");
        this.serviceName = (String)logMap.get("serviceName");
        this.logObjectId = (String)logMap.get("logObjectId");
        this.logMethodName = (String)logMap.get("logMethodName");

    }

    @Override
    protected String uniqueLogInfo() {
        return this.concatInfo(super.uniqueLogInfo(), logUserId,serviceName, logObjectId, logMethodName);
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("logUserId", this.logUserId);
        jsonObject.put("serviceName",serviceName);
        jsonObject.put("logMethodName", this.logMethodName);
        jsonObject.put("logObjectId", this.logObjectId);

        return jsonObject;
    }
}
