package com.badsmell.base;

import lombok.Data;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-22 14:45
 */
@Data
public class ServiceRT {
    public String serviceName;
    public long currentWindowRT;
    public long lastWindowRT;

    public ServiceRT(String serviceName, long currentWindowRT, long lastWindowRT) {
        this.serviceName = serviceName;
        this.currentWindowRT = currentWindowRT;
        this.lastWindowRT = lastWindowRT;
    }
}
