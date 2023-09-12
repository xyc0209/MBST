package com.mbs.common.log;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Map;


@Getter
@Setter
public abstract class MMetricsBaseLog extends MBaseLog {
    protected String logHostname;
    protected Long logCpuUsage;
    protected Long logRamUsage;

    @Override
    protected String uniqueLogInfo() {
        return this.concatInfo(super.uniqueLogInfo(), logHostname, logCpuUsage.toString(), logRamUsage.toString());
    }

    @Override
    protected String[] fillInfo(String[] strArr) {
        String[] leftStrArr = super.fillInfo(strArr);
        if (leftStrArr != null) {
            this.logHostname = leftStrArr[0];
            this.logCpuUsage = Long.decode(leftStrArr[1]);
            this.logRamUsage = Long.decode(leftStrArr[2]);
            return this.getUnusedStrArr(leftStrArr, 3);
        }
        return null;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("logHostname", logHostname);
        jsonObject.put("logCpuUsage", logCpuUsage);
        jsonObject.put("logRamUsage", logRamUsage);
        return jsonObject;
    }

    @Override
    protected void fillInfo(Map<String, Object> logMap) {
        super.fillInfo(logMap);
        this.logHostname = (String) logMap.get("logHostname");
        this.logCpuUsage = Long.valueOf(logMap.get("logCpuUsage").toString());
        this.logRamUsage = Long.valueOf(logMap.get("logRamUsage").toString());

    }
}
