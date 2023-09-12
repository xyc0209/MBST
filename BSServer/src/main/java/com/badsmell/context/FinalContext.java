package com.badsmell.context;

import com.smelldetection.base.context.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-30 15:50
 */
@Component
@Data
public class FinalContext {
    public int servicesCount;
    public  Set<String> serviceList;
    public  double qualityScore;
    public Map<String, Double> qualityScoreDetail;
    public  double runtimeQualityScore;
    public CircleDependencyContext circleDependencyContext;
    public Set<String> sharedDatabaseSet;
    public Set<String> serviceIntimacySet;
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
    public  SharedLibraryContext sharedLibraryContext;
    public TMSContext tmsContext;
    public  UnusedContext unusedContext;
    public WrongCutContext wrongCutContext;
    public AvailabilityContext availabilityContext;
    public UnevenSvcContext unevenSvcContext;
    public UnevenIfcContext unevenIfcContext;
    public UnevenResContext unevenResContext;
}
