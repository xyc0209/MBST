package com.badsmell.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: xyc
 * @date: 2025-05-14 10:45
 */
@Data
@NoArgsConstructor
public class CustomRequestItem {
    public String servicesPath;
    public boolean TS;
    public boolean UR;

    public CustomRequestItem(String servicesPath, boolean TS, boolean UR) {
        this.servicesPath = servicesPath;
        this.TS = TS;
        this.UR = UR;
    }
}