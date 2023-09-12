package com.badsmell.context;

import lombok.Data;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-09 14:49
 */
@Data
public class ServiceAvailabilityContext {

    public String serviceName;
    public long rtInMillis;

    public ServiceAvailabilityContext(String serviceName, long RTInMillis) {
        this.serviceName = serviceName;
        this.rtInMillis = RTInMillis;
    }

    @Override
    public String toString() {
        return "ServiceAvailabilityContext{" +
                "serviceName='" + serviceName + '\'' +
                ", rtInMillis=" + rtInMillis +
                '}';
    }
}
