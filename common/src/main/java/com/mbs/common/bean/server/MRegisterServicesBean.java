package com.mbs.common.bean.server;

import com.mbs.common.base.MService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MRegisterServicesBean {
    private List<MService> serviceList;
    private boolean clearOldFlag;
}
