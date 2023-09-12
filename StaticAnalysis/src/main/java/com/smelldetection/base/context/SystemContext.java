package com.smelldetection.base.context;

import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-27 15:30
 */
@Data
public class SystemContext {

    public int servicesCount;
    public ServiceContext serviceContext;
    public ApiVersionContext apiVersionContext;
    public CyclicReferenceContext cyclicReferenceContext;
    public ESBServiceContext esbServiceContext;
    public GateWayContext gateWayContext;
    public GodContext godContext;
    public GreedyContext greedyContext;
    public HardCodeContext hardCodeContext;
    public HubContext hubContext;
    public MultiPathContext multiPathContext;
    public ScatteredContext scatteredContext;
    public  SharedDatabaseContext sharedDatabaseContext;
    public  SharedLibraryContext sharedLibraryContext;
    public TMSContext tmsContext;
    public  UnusedContext unusedContext;
    public WrongCutContext wrongCutContext;
    public Map<String, Long> times;
}
