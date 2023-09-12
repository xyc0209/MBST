package com.badsmell.base;

import lombok.Data;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-09 19:57
 */
@Data
public class Factor {
    public int sum;
    public Map<String, Integer> svcFactor;

    public Factor(int sum, Map<String, Integer> svcFactor) {
        this.sum = sum;
        this.svcFactor = svcFactor;
    }
}
