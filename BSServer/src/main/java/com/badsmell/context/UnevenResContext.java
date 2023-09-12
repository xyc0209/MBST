package com.badsmell.context;


import com.badsmell.analysisentity.cdabs.ReceivedMetricLog;
import lombok.Data;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-10 17:05
 */
@Data
public class UnevenResContext {
    public boolean status;
    public double cpuSoh;
    public double ramSoh;
    public Map<String, Map<String, ReceivedMetricLog>> unevenCpuInstance;
    public Map<String, Map<String, ReceivedMetricLog>> unevenRamInstance;

    public UnevenResContext(double cpuSoh, double ramSoh, Map<String, Map<String, ReceivedMetricLog>> unevenCpuInstance, Map<String, Map<String, ReceivedMetricLog>> unevenRamInstance) {
        this.cpuSoh = cpuSoh;
        this.ramSoh = ramSoh;
        this.unevenCpuInstance = unevenCpuInstance;
        this.unevenRamInstance = unevenRamInstance;
    }
}
