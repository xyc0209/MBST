package com.smelldetection.base.context;

import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2025-05-15 14:46
 */
@Data
public class CBContext {
    public boolean hasCircuitBreaker;
    public Map<String, Boolean> hystrixUsage;
    public Map<String, Boolean> resilience4jUsage;
    public Map<String, Boolean> sentinelUsage;

}