package com.smelldetection.base.context;

import com.smelldetection.base.Enum.GateWayType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-25 08:53
 */
@Data
@AllArgsConstructor
public class GateWayContext {
    public boolean status;
    public boolean hasGateWay;
    public GateWayType type;

    public GateWayContext(){
        this.hasGateWay = false;
        this.status = true;
    }

}
