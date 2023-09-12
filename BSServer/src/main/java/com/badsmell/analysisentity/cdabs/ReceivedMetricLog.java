package com.badsmell.analysisentity.cdabs;

import lombok.Data;
import org.joda.time.DateTime;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-10 14:17
 */
@Data
public class ReceivedMetricLog {
    private DateTime logDateTime;
    private String logType;
    private String logIpAddr;
    private String podName;  //the pod name which was metriced
    private String CpuUsage;  // the usage of cpu
    private String RamUsage;  // the usage of ram
    /**
     * @param record
     * @return analysis
     */
    public  boolean resolveFromString(String record){

        String[] lists = record.split("\\|",6);
        if (lists.length!=6)
            return false;

        this.setLogDateTime(DateTime.parse(lists[0]));
        this.setLogType(lists[1]);
        this.setLogIpAddr(lists[2]);
        this.setPodName(lists[3]);
        this.setCpuUsage(lists[4]);
        this.setRamUsage(lists[5]);
        return true;

    }
}
