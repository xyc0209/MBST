package com.mbs.common.base;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MConnectionJson {
    private String successor;
    private String predecessor;
    private MNodeConnectionInfo connection;
}