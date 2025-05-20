package com.smelldetection.controller;


import com.smelldetection.base.context.*;
import com.smelldetection.base.context.*;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.service.*;
import com.github.javaparser.ParseException;
import com.smelldetection.service.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("detect/v1.0")

@CrossOrigin
public class DetectionController {

    @Autowired
    public SharedLibraryService sharedLibraryService;
    @Autowired
    public HardCodeService hardCodeService;

    @Autowired
    public SharedDatabaseAndServiceIntimacyService sharedDatabaseAndServiceIntimacyService;

    @Autowired
    public UnVersionedApiService unversionedApiService;

    @Autowired
    public NoGateWayService noGateWayService;

    @Autowired
    public WrongServiceCutService wrongServiceCutService;

    @Autowired
    public  UnusedService unusedService;
    @Autowired
    public GodService godService;

    @Autowired
    public CyclicReferenceService cyclicReferenceService;

    @Autowired
    public MultipathImplementationService multipathImplementationService;

    @Autowired
    public ESBService esbService;

    @Autowired
    public TooMuchStandardsService tooMuchStandardsService;

    @Autowired
    public GreedyService greedyService;

    @Autowired
    public HubService hubService;

    @Autowired
    public  ScatteredService scatteredService;

    @Autowired
    public CBService cbService;

    @Autowired
    public FileFactory fileFactory;

    @RequestMapping(path = "/sharedLibrary",method = RequestMethod.POST)
    public SharedLibraryContext SharedLibrary(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException {
        System.out.println(requestItem.getServicesPath());
        return sharedLibraryService.getSharedLibraries(requestItem);
    }



    @RequestMapping(path = "/circuitBreaker",method = RequestMethod.POST)
    public CBContext CircuitBreaker(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException {
        System.out.println(requestItem.getServicesPath());
        return cbService.getCBLibraries(requestItem);
    }

    @RequestMapping(path = "/sharedDatabaseandServiceIntimacy",method = RequestMethod.POST)
    public SharedDatabaseContext SharedDatabaseAndServiceIntimacy(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException {
        System.out.println(requestItem.getServicesPath());
        return sharedDatabaseAndServiceIntimacyService.getsharedDatabaseandServiceIntimacy(requestItem);
    }

    @RequestMapping(path = "/noApiVersion",method = RequestMethod.POST)
    public ApiVersionContext UnVersionedApis(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException, ClassNotFoundException, ParseException {
        System.out.println(requestItem.getServicesPath());
        return unversionedApiService.getUnVersionedApis(requestItem);
    }

    @RequestMapping(path = "/cyclicReferences",method = RequestMethod.POST)
    public CyclicReferenceContext CyclicReferences(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException, ClassNotFoundException, ParseException {
        System.out.println(requestItem.getServicesPath());
        return cyclicReferenceService.getCyclicReference(requestItem);
    }

    @RequestMapping(path = "/noGateWay", method = RequestMethod.POST)
    public GateWayContext NoGateWay(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException {
        return noGateWayService.isExistGateWay(requestItem);
    }

    @RequestMapping(path = "/wrongServiceCut", method = RequestMethod.POST)
    public WrongCutContext WrongServiceCut(@RequestBody RequestItem requestItem) throws IOException {
        return wrongServiceCutService.getWrongServiceCutServices(requestItem);

    }
    @RequestMapping(path = "/getGodServices", method = RequestMethod.POST)
    public GodContext GetGodServices(@RequestBody RequestItem requestItem) throws IOException {
        return godService.getGodServices(requestItem);
    }
    @RequestMapping(path = "/unUsedInterfaceAndAbstractClass", method = RequestMethod.POST)
    public UnusedContext UnUsedInterfaceAndAbstractClass(@RequestBody RequestItem requestItem) throws IOException {
        return unusedService.getUnusedInterfaceAndAbstractClass(requestItem);
    }

    @RequestMapping(path = "/hardcodeIPandPort",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public HardCodeContext HardCOdeIPandPort(@RequestBody RequestItem requestItem) throws IOException {
        return hardCodeService.getHardCode(requestItem);
    }

    @RequestMapping(path = "/multipath",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public MultiPathContext MultipathImplementation(@RequestBody RequestItem requestItem) throws IOException {
        return multipathImplementationService.getMultipathClass(requestItem);
    }

    @RequestMapping(path = "/getESBServices",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public ESBServiceContext GetESBServices(@RequestBody RequestItem requestItem) throws IOException {
        return esbService.getESBServices(requestItem);
    }

    @RequestMapping(path = "/tooMuchStandards",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public TMSContext GetTooMuchStandards(@RequestBody RequestItem requestItem) throws IOException {
        return tooMuchStandardsService.getTMSServices(requestItem);
    }

    @RequestMapping(path = "serviceGreedy",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public GreedyContext GetSvcGreedy(@RequestBody RequestItem requestItem) throws IOException {
        return greedyService.getGreedySvc(requestItem);
    }

    @RequestMapping(path = "hubClass",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public HubContext getHubClass(@RequestBody RequestItem requestItem) throws IOException {
        return hubService.getHubClass(requestItem);
    }
    @RequestMapping(path = "scatteredFunctionality",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public ScatteredContext getSFServices(@RequestBody RequestItem requestItem) throws IOException{
        return scatteredService.getSFServices(requestItem);
    }


    @RequestMapping(path = "getStaticAnalysisResults",method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public SystemContext getStaticAnalysisResults(@RequestBody RequestItem requestItem) throws XmlPullParserException, IOException, ParseException, ClassNotFoundException {
        SystemContext systemContext = new SystemContext();
        systemContext.setServicesCount(fileFactory.getServicesCount(requestItem.getServicesPath()).size());
        systemContext.setServiceContext(fileFactory.getServiceList(requestItem.getServicesPath()));
        Map<String, Long> times = new HashMap<>();
        long cur =System.currentTimeMillis();
        systemContext.setSharedLibraryContext(sharedLibraryService.getSharedLibraries(requestItem));
        long now = System.currentTimeMillis();
        times.put("sharedLibrary",now - cur);

        cur =System.currentTimeMillis();
        systemContext.setSharedDatabaseContext(sharedDatabaseAndServiceIntimacyService.getsharedDatabaseandServiceIntimacy(requestItem));
        now = System.currentTimeMillis();
        times.put("sharedDatabase",now - cur);

        cur =System.currentTimeMillis();
        systemContext.setApiVersionContext(unversionedApiService.getUnVersionedApis(requestItem));
        now = System.currentTimeMillis();
        times.put("unversionedApi",now - cur);

        cur =System.currentTimeMillis();
        systemContext.setCyclicReferenceContext(cyclicReferenceService.getCyclicReference(requestItem));
        now = System.currentTimeMillis();
        times.put("cyclicReference",now - cur);

        cur =System.currentTimeMillis();
        systemContext.setGateWayContext(noGateWayService.isExistGateWay(requestItem));
        now = System.currentTimeMillis();
        times.put("noGateWay",now - cur);
        systemContext.setWrongCutContext(wrongServiceCutService.getWrongServiceCutServices(requestItem));
        systemContext.setGodContext(godService.getGodServices(requestItem));

        cur =System.currentTimeMillis();
        systemContext.setUnusedContext(unusedService.getUnusedInterfaceAndAbstractClass(requestItem));
        now = System.currentTimeMillis();
        times.put("unused",now - cur);

        cur =System.currentTimeMillis();
        systemContext.setHardCodeContext(hardCodeService.getHardCode(requestItem));
        now = System.currentTimeMillis();
        times.put("hardCode",now - cur);

        cur =System.currentTimeMillis();
        systemContext.setMultiPathContext(multipathImplementationService.getMultipathClass(requestItem));
        now = System.currentTimeMillis();
        times.put("multipath",now - cur);
        systemContext.setTimes(times);
        systemContext.setEsbServiceContext(esbService.getESBServices(requestItem));
//        systemContext.setTmsContext(tooMuchStandardsService.getTMSServices(requestItem));
        cur =System.currentTimeMillis();
        systemContext.setGreedyContext(greedyService.getGreedySvc(requestItem));
        now = System.currentTimeMillis();
        times.put("greedy",now - cur);
       systemContext.setHubContext(hubService.getHubClass(requestItem));
        cur =System.currentTimeMillis();
       systemContext.setScatteredContext(scatteredService.getSFServices(requestItem));
        now = System.currentTimeMillis();
        times.put("scatter",now - cur);
        return systemContext;
    }
}
