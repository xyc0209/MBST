package com.badsmell.context;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-10 09:42
 */
@Data
public class UnevenIfcContext {
    public boolean status;
    public double soh;
    public Map<String, List<String>> unevenInterface;

    public UnevenIfcContext(double soh, Map<String, List<String>> unevenInterface) {
        this.soh = soh;
        this.unevenInterface = unevenInterface;
    }
}
