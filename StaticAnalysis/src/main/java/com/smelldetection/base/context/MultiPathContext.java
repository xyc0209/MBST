package com.smelldetection.base.context;

import com.smelldetection.base.item.MultiPathItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-02-24 15:34
 */
@Data
public class MultiPathContext {
    public boolean status;
    public List<MultiPathItem> multiItems;

    public MultiPathContext(){
        this.multiItems = new ArrayList<>();
    }
}
