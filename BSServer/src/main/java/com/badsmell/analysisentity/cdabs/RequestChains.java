package com.badsmell.analysisentity.cdabs;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RequestChains {

    private String RequestUser;
    //<traceId,logrecords>
    private Map<String,List<ReceivedLog>> requestChainSet;



}
