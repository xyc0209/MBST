package com.mbs.common.bean;

import com.mbs.common.base.MResponse;
import com.mbs.common.base.MUserDemand;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MUserRequestBean {
    private MUserDemand userDemand;
    private MResponse data;

    @Override
    public String toString() {
        return "MUserRequestBean{" +
                "userDemand=" + userDemand +
                ", data=" + data +
                '}';
    }
}
