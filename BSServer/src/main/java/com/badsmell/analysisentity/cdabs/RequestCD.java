package com.badsmell.analysisentity.cdabs;

import com.badsmell.analysisentity.cdabs.CircleDescribeTree;
import com.badsmell.analysisentity.graph.Digraph;
import lombok.Data;


/**
 * author：yang
 * date: 2022/12/19
 * 针对每一次完整的请求链，存储循环依赖判断情况
 */
@Data

public class RequestCD {
    //
    private Boolean isCircle;
    private CircleDescribeTree treeRoot;//成环才将请求链路附带
    private Digraph digraph;  //成环才将图附带

}
