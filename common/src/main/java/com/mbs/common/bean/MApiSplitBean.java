package com.mbs.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MApiSplitBean {
    private String objectId;
    private String functionName;
    private Boolean status;
}
