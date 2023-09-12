package com.badsmell.context;

import com.badsmell.base.ServiceRT;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: Record services with longer response times than in the last time window
 * @author: xyc
 * @date: 2023-03-22 14:36
 */
@Data
public class RTIncreasedContext {
    public List<ServiceRT> rtIncreasedSvcLists;

    public  RTIncreasedContext(){
        this.rtIncreasedSvcLists = new ArrayList<>();
    }
}
