package com.mbs.mclient.base;

import com.mbs.mclient.utils.StringUtils;


public class MResource extends MObject {

    public MResource() {
        if (this.getId() == null) {
            this.setId(StringUtils.generateMResourceId());
        }
    }
}
