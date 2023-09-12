package com.badsmell.context;

import lombok.Data;
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-09 20:09
 */
@Data
public class AvailabilityContext {

    public boolean status;

    public double soh;
    public List<ServiceAvailabilityContext> svcWithLowAvailability;

    public AvailabilityContext(double soh, List<ServiceAvailabilityContext> svcWithLowAvailability) {
        this.soh = soh;
        this.svcWithLowAvailability = svcWithLowAvailability;
    }
}
