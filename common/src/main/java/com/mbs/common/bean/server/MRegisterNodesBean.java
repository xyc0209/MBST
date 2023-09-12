package com.mbs.common.bean.server;

import com.mbs.common.base.MConnectionJson;
import com.mbs.common.base.MServerNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MRegisterNodesBean {
    private List<MServerNode> nodeList;
    private List<MConnectionJson> connectionInfoList;
}
