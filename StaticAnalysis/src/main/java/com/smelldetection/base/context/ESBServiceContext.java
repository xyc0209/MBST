package com.smelldetection.base.context;

import com.smelldetection.base.item.SvcCallDetail;
import lombok.Data;

import java.util.HashMap;

/**
 * @description:
 * @author: xyc
 * @date: 2023-02-24 21:47
 */
@Data
public class ESBServiceContext {
    public boolean status;
    public HashMap<String, SvcCallDetail> result;

}
