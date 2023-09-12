package com.mbs.common.log;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Map;


@Getter
@Setter
public class MFunctionCalledLog extends MServiceBaseLog {

    private String traceId;
    private String id;
    private String parentId;
    private String logFromIpAddr;
    private Integer logFromPort = 0;

    public MFunctionCalledLog() {
        this.logType = MLogType.FUNCTION_CALL;
    }

    @Override
    protected String[] fillInfo(String[] strArr) {
        String[] leftStrArr = super.fillInfo(strArr);
        if (leftStrArr != null) {
            this.traceId = leftStrArr[0];
            this.id = leftStrArr[1];
            this.parentId = leftStrArr[2];
            this.logFromIpAddr = leftStrArr[3];
            this.logFromPort = Integer.valueOf(leftStrArr[4]);
            return getUnusedStrArr(leftStrArr, 5);
        }
        return null;
    }

    @Override
    protected void fillInfo(Map<String, Object> logMap) {
        super.fillInfo(logMap);
        this.traceId = (String) logMap.get("traceId");
        this.id = (String) logMap.get("id");
        this.parentId = (String) logMap.get("parentId");
        this.logFromIpAddr = (String) logMap.get("logFromIpAddr");
        this.logFromPort = (Integer) logMap.get("logFromPort");
    }

    @Override
    protected String uniqueLogInfo() {
        return this.concatInfo(super.uniqueLogInfo(), traceId, id, parentId, logFromIpAddr, logFromPort.toString());
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("traceId", this.traceId);
        jsonObject.put("id", this.id);
        jsonObject.put("parentId", this.parentId);
        jsonObject.put("logFromIpAddr", this.logFromIpAddr);
        jsonObject.put("logFromPort", this.logFromPort);
        return jsonObject;
    }
}
