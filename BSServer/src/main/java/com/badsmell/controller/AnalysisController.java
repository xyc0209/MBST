package com.badsmell.controller;

import com.badsmell.base.MyLogger;
import com.badsmell.base.RequestItem;
import com.badsmell.context.*;
import com.badsmell.service.*;
import com.badsmell.utils.CircleDependencyAnalysisUtils;
import com.badsmell.utils.ElasticSearchUtils;
import com.badsmell.utils.SortByDate;
import com.badsmell.utils.TimeWindowUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.badsmell.analysisentity.cdabs.RequestUserSet;
import com.smelldetection.base.context.SystemContext;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-06 21:39
 */
@RestController
@RequestMapping("judge/v1.0")

public class AnalysisController {
    public RestHighLevelClient esClient;
    public String[] indexArr;

    @Autowired
    public AvailabilityService availabilityService;

    @Autowired
    public UnevenService unevenService;

    @Autowired
    public UnevenInterfaceService unevenInterfaceService;

    @Autowired
    public UnevenResService unevenResService;

    @Autowired
    public SharedDatabaseService sharedDatabaseService;

    @Autowired
    public ServiceIntimacyService serviceIntimacyService;
    @Autowired
    public CaculateService caculateService;
    @Autowired
    public FinalService finalService;
    @Autowired
    public FinalContext finalContext;

    public MyLogger logger;

    public AnalysisController() throws IOException {
        logger = new MyLogger("noshareddatabase-food.txt");
        this.esClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("172.16.17.37", 30001)
                )
        );
        this.indexArr = new String[1];
        indexArr[0] = "k8s-system-log-*";
    }

    @RequestMapping(path = "getAnalysisResults", method = RequestMethod.POST)
    public FinalContext getAnalysisResults(@RequestBody RequestItem requestItem) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
//        params.put("servicesPath", "/Users/yongchaoxing/Desktop/experiment/3-circlereference");
        params.put("servicesPath", requestItem.getServicesPath());
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = "http://localhost:8099/detect/v1.0/getStaticAnalysisResults";
        SystemContext response = restTemplate.postForObject(url, requestEntity, SystemContext.class);
        //Calculate coverage with only static analysis
        caculateService.processSystemContext(response);
        //Determine shared database based on runtime data
        judgeServiceRT();
        caculateService.judgeSharedDatabase();
        //Determine service Intimacy based on runtime data
        judgeServiceIntimacy();
        caculateService.judgeServiceIntimacy();
        //Determine circle Dependency based on runtime data
        caculateService.processCircleDependencies(judgeCircleDependencies());
        //caculate quality score
        caculateService.caculateQualityScore();
        System.out.println("------"+CaculateService.qualityScore);
//        caculateService.caculateRuntimeScore(serviceAvailability(), unevenlyUsedSvc(), unevenInterface(),unevenResSvc());
//        System.out.println("------"+CaculateService.runtimeQualityScore);
        finalService.setFinalContext(response, caculateService);
        return finalContext;
    }
    @RequestMapping(path = "/judgeCircle",method = RequestMethod.GET)
    public CircleDependencyContext judgeCircleDependencies(){
        System.out.println("time "+ DateTime.now().minusHours(0).minusMinutes(10));
        List<String> result =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(58), DateTime.now(), "FUNCTION_CALL");
        SortByDate sortByDate = new SortByDate();
        Collections.sort(result, sortByDate);
        for (int i=0;i<result.size();i++){
            System.out.println(result.get(i));
        }
        RequestUserSet requestUserSet = new RequestUserSet();
        requestUserSet.handleLogs(result);
        System.out.println(requestUserSet.toString());
        System.out.println("+++++++++++++++++++++");
        return CircleDependencyAnalysisUtils.analysisCDABS(requestUserSet);

    }

    @RequestMapping(path = "/getLowAvailability",method = RequestMethod.GET)
    public  AvailabilityContext serviceAvailability() throws IOException {
        List<String> result1 =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(0).minusSeconds(10), DateTime.now(), "FUNCTION_CALL");
        List<String> result2 =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(0).minusSeconds(10), DateTime.now(), "FUNCTION_CALL_END");
        result1.addAll(result2);
//        for (int i=0;i<result1.size();i++){
//            System.out.println(result1.get(i));
//        }
        return availabilityService.findLowAvailability(result1, logger);

    }

    @RequestMapping(path = "/getUnevenUsedSvc",method = RequestMethod.GET)
    public UnevenSvcContext unevenlyUsedSvc(){
        List<String> result =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(8).minusMinutes(0), DateTime.now(), "FUNCTION_CALL");
        return unevenService.getUnevenService(result);

    }

    @RequestMapping(path = "/getUnevenInterface",method = RequestMethod.GET)
    public UnevenIfcContext unevenInterface(){
        List<String> result =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(8).minusMinutes(0), DateTime.now(), "FUNCTION_CALL");
        for(String log: result)
            System.out.println(log);
        return unevenInterfaceService.getUnevenInterface(result);

    }

    @RequestMapping(path = "/getUnevenResourceSvc", method = RequestMethod.GET)
    public UnevenResContext unevenResSvc(){
        List<String> result =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(10), DateTime.now(), "CONTAINER_METRICS_LOG");
        List<String> callResult =  ElasticSearchUtils.getLogsBetween(this.esClient, this.indexArr, DateTime.now().minusHours(8).minusMinutes(0), DateTime.now(), "FUNCTION_CALL");
        System.out.println("size"+result.size());
        for(String log: result)
            System.out.println(log);
        return unevenResService.getUnevenResService(result, callResult);
//        return unevenInterfaceService.getUnevenInterface(result);
    }

    @RequestMapping(path = "/sql",method = RequestMethod.GET)
    public ServiceIntimacyContext judgeServiceIntimacy(){
        System.out.println("-----");
        List<String> result =  TimeWindowUtils.getSqlLog(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(52), DateTime.now());

        for(String log: result)
            System.out.println(log);
        return serviceIntimacyService.judgeIntimateServices(result);

    }

    @RequestMapping(path = "/judgeServiceRT",method = RequestMethod.GET)
    public RTIncreasedContext judgeServiceRT() throws IOException {
        List<String> currentWindowResult = TimeWindowUtils.getCurrentWindowLog(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(0).minusSeconds(10), DateTime.now(), "FUNCTION_CALL", "FUNCTION_CALL_END");
        List<String> lastWindowResult = TimeWindowUtils.getCurrentWindowLog(this.esClient, this.indexArr, DateTime.now().minusHours(0).minusMinutes(0).minusSeconds(20), DateTime.now().minusHours(0).minusMinutes(0).minusSeconds(10), "FUNCTION_CALL", "FUNCTION_CALL_END");
        return sharedDatabaseService.judgeAllowed(currentWindowResult, lastWindowResult,logger);

    }

    @RequestMapping(path ="/scheduleServiceAvailability",method = RequestMethod.GET)
    public void scheduleServiceAvailability() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    AvailabilityContext availability = serviceAvailability();
                    // do something with the availability object here
                } catch (Exception e) {
                    e.printStackTrace();
                    // handle the exception here
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    RTIncreasedContext rtIncreasedContext = judgeServiceRT();
                    // do something with the availability object here
                } catch (Exception e) {
                    e.printStackTrace();
                    // handle the exception here
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }



    public static void main(String[] args) {
    }
}
