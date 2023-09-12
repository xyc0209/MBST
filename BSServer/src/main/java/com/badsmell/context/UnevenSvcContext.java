package com.badsmell.context;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-09 21:00
 */
@Data
public class UnevenSvcContext {
    public boolean status;
    public double soh;
    public List<String> serviceList;

    public UnevenSvcContext(double soh, List<String> serviceList) {
        this.soh = soh;
        this.serviceList = serviceList;
    }
}
