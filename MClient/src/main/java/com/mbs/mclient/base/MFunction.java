package com.mbs.mclient.base;

import com.mbs.mclient.utils.StringUtils;


public class MFunction extends MObject {

    public MFunction() {
        if (this.getId() == null) {
            this.setId(StringUtils.generateMFunctionId());
        }
    }
}
