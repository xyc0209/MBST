package com.mbs.mclient.core;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.netflix.appinfo.InstanceInfo;
//import com.netflix.discovery.EurekaClient;
//import com.netflix.discovery.shared.Application;
import com.mbs.mclient.base.MObject;
import com.mbs.mclient.utils.PropertiesUtils;
import com.mbs.mclient.utils.RequestUtils;
import com.mbs.common.bean.MApiSplitBean;
import com.mbs.common.bean.MGetRemoteUriRequest;
import com.mbs.common.bean.MInstanceRestInfoBean;
import com.mbs.common.log.MBaseLog;
import com.mbs.common.log.MFunctionCallEndLog;
import com.mbs.common.log.MFunctionCalledLog;
import com.mbs.common.utils.MLogUtils;
import com.mbs.common.utils.MRequestUtils;
import com.mbs.common.utils.MUrlUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;


@Service
public class MClientSkeleton {

    private static volatile MClientSkeleton instance;
    @Getter
    private Map<String, MObject> mObjectMap;
    @Getter
    private Map<String, String> parentIdMap;
    @Getter
    private Map<String, Set<String>> objectId2ApiSet;

    private Map<String, Map<String, MInstanceRestInfoBean>> restInfoMap;
    private Map<String, Map<String, Boolean>> apiContinueMap;
    private static Logger logger = LogManager.getLogger(MClientSkeleton.class);
    private static final String LogstashIp = "172.16.17.38";
    private static final int LogstashPort = 32001;


//    @Setter
//    private EurekaClient discoveryClient;
    private static Environment env;
    private static String serviceName;

    @Setter
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private MClientSkeleton() {
        this.mObjectMap = new HashMap<>();
        this.parentIdMap = new HashMap<>();
        this.objectId2ApiSet = new HashMap<>();
        this.restInfoMap = new HashMap<>();
        this.apiContinueMap = new HashMap<>();
    }

    @Autowired
    public void setEnvironment(Environment env) {
        MClientSkeleton.env = env;
        MClientSkeleton.serviceName = env.getProperty("spring.application.name");
    }

//    private static ApplicationContext applicationContext;
//    private static Environment env;
//
//    @Autowired
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        MClientSkeleton.applicationContext = applicationContext;
//        MClientSkeleton.env = applicationContext.getEnvironment();
//    }

    public static MClientSkeleton getInstance() {
        if (instance == null) {
            synchronized (MClientSkeleton.class) {
                if (instance == null) {
                    instance = new MClientSkeleton();
                    serviceName = PropertiesUtils.getValue("spring.application.name");
                }
            }
        }
        return instance;
    }

    /*
     * register object
     */
    public void registerMObject(MObject object) {
        if (this.mObjectMap.containsKey(object.getId())) {
            logger.warn("MObject " + object.getId() + " has been registered before !!!");
        } else {
            this.mObjectMap.put(object.getId(), object);
        }
    }

    /*
     * register the parent id of object
     */
    public void registerParent(MObject object, String parentId) {
        if (this.mObjectMap.containsKey(object.getId())) {
            this.parentIdMap.put(object.getId(), parentId);
        } else {
            logger.warn("MObject " + object.getId() + " not registered");
        }
    }

    public void printParentIdMap() {
        logger.debug(this.parentIdMap.toString());
    }

    public List<String> getMObjectIdList() {
        return new ArrayList<>(this.mObjectMap.keySet());
    }

    /*
     * add an info bean
     */
    public void addRestInfo(MInstanceRestInfoBean infoBean) {
        if (infoBean.getRestAddress() == null) {
            this.removeRestInfo(infoBean);
            return;
        }

        if (!this.restInfoMap.containsKey(infoBean.getObjectId())) {
            this.restInfoMap.put(infoBean.getObjectId(), new HashMap<>());
        }
        this.restInfoMap.get(infoBean.getObjectId()).put(infoBean.getFunctionName(), infoBean);
    }

    /*
     * delete an info bean
     */
    private void removeRestInfo(MInstanceRestInfoBean infoBean) {
        if (this.restInfoMap.containsKey(infoBean.getObjectId())) {
            this.restInfoMap.get(infoBean.getObjectId()).remove(infoBean.getFunctionName());
        }
    }

    /**
     * Get all Rest info
     * @return List
     */
    public List<MInstanceRestInfoBean> getRestInfoBeanList() {
        List<MInstanceRestInfoBean> restInfoBeans = new ArrayList<>();
        for (String mObjectId : this.restInfoMap.keySet()) {
            restInfoBeans.addAll(this.restInfoMap.get(mObjectId).values());
        }
        return restInfoBeans;
    }

    /**
     * It will be used by MApiType annotation
     * @param mObjectId: the id of MObject
     * @param functionName: the function will be used/called
     * @return boolean
     */
    public static boolean isRestNeeded(String mObjectId, String functionName) {
        return MClientSkeleton.getInstance().checkIfHasRestInfo(mObjectId, functionName);
    }

    public void setApiContinueStatus(MApiSplitBean apiSplitBean) {
        if (!this.apiContinueMap.containsKey(apiSplitBean.getObjectId())) {
            this.apiContinueMap.put(apiSplitBean.getObjectId(), new HashMap<>());
        }
        this.apiContinueMap.get(apiSplitBean.getObjectId()).put(apiSplitBean.getFunctionName(), apiSplitBean.getStatus());
    }

    public static boolean checkIfContinue(String mObjectId, String functionName) {
        if (!MClientSkeleton.getInstance().apiContinueMap.containsKey(mObjectId)) return true;
        return MClientSkeleton.getInstance().apiContinueMap.get(mObjectId).getOrDefault(functionName, true);
    }

    public static void logFunctionCall(String mObjectId, String functionName, HttpServletRequest request) {
        MFunctionCalledLog serviceBaseLog = new MFunctionCalledLog();
        serviceBaseLog.setLogDateTime(DateTime.now());
        serviceBaseLog.setLogMethodName(functionName);
        serviceBaseLog.setLogObjectId(mObjectId);
        System.out.println("service Name："+serviceName);
        serviceBaseLog.setServiceName(serviceName);

        if (request != null) {
            serviceBaseLog.setLogFromIpAddr(request.getRemoteAddr());
            serviceBaseLog.setLogFromPort(request.getRemotePort());
//            serviceBaseLog.setLogUserId(request.getHeader("userId"));
            serviceBaseLog.setTraceId(request.getHeader("traceId"));
            serviceBaseLog.setId(request.getHeader("id"));
            serviceBaseLog.setParentId(request.getHeader("parentId"));
            serviceBaseLog.setLogIpAddr(request.getLocalAddr());
        }
        // todo: get the ip address and set it to serviceBaseLog.logIpAddr
        MLogUtils.log(serviceBaseLog);
    }

    public static void logFunctionCallEnd(String mObjectId, String functionName, HttpServletRequest request) {
        MFunctionCallEndLog serviceBaseLog = new MFunctionCallEndLog();
        serviceBaseLog.setLogDateTime(DateTime.now());
        serviceBaseLog.setLogMethodName(functionName);
        serviceBaseLog.setLogObjectId(mObjectId);
        serviceBaseLog.setServiceName(serviceName);
        if (request != null) {
            System.out.println("trac id:------"+request.getHeader("traceId"));
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                System.out.println(headerName + ": " + headerValue);
            }

            serviceBaseLog.setTraceId(request.getHeader("traceId"));
            serviceBaseLog.setId(request.getHeader("id"));
            serviceBaseLog.setParentId(request.getHeader("parentId"));
            serviceBaseLog.setLogFromIpAddr(request.getRemoteAddr());
            serviceBaseLog.setLogFromPort(request.getRemotePort());
//            serviceBaseLog.setLogUserId(request.getHeader("userId"));
            serviceBaseLog.setLogIpAddr(request.getLocalAddr());

        }
        // todo: get the ip address and set it to serviceBaseLog.logIpAddr
        System.out.println("---test---");
        System.out.println("json"+MBaseLog.convertLog2JsonObejct(serviceBaseLog).toString());
        MLogUtils.log(serviceBaseLog);
    }

    /**
     * It will be used by MApiType annotation
     * @param mObjectId: the id of MObject
     * @param functionName: the function will be used/called
     * @param args: the arguments
     * @return Object
     */
//    public static Object restRequest(String mObjectId, String functionName, String returnTypeStr, Object... args) {
//        List<String> paramNameList = new ArrayList<>(args.length / 2);
//        List<Object> paramValueList = new ArrayList<>(args.length / 2);
//        for (int i = 0; i < args.length; i += 2) {
//            paramNameList.add((String)args[i]);
//            paramValueList.add(args[i+1]);
//        }
//        String paramJsonStr = RequestUtils.methodParamToJsonString(paramNameList, paramValueList);
//
//        if (MClientSkeleton.getInstance().discoveryClient != null) {
//            Application clusterAgent = MClientSkeleton.getInstance().discoveryClient.getApplication("MClusterAgent");
//            if (clusterAgent != null) {
//                List<InstanceInfo> clusterAgentInstances = clusterAgent.getInstances();
//                if (clusterAgentInstances.size() > 0) {
//                    // request MClusterAgent for remote uri
//                    URI requestUri = MUrlUtils.getMClientRequestRemoteUri(clusterAgentInstances.get(0).getIPAddr(), clusterAgentInstances.get(0).getPort());
//                    logger.debug(requestUri);
//                    if (requestUri != null) {
//                        String rawPatterns = null;
//                        Map<RequestMappingInfo, HandlerMethod> mapping = MClientSkeleton.getInstance().requestMappingHandlerMapping.getHandlerMethods();
//                        for (RequestMappingInfo mappingInfo : mapping.keySet()) {
//                            if (mapping.get(mappingInfo).getMethod().getName().equals(functionName)) {
//                                rawPatterns = mappingInfo.getPatternsCondition().toString();
//                                break;
//                            }
//                        }
//                        MGetRemoteUriRequest getRemoteUriRequest = new MGetRemoteUriRequest();
//                        getRemoteUriRequest.setFunctionName(functionName);
//                        getRemoteUriRequest.setObjectId(mObjectId);
//                        getRemoteUriRequest.setRawPatterns(rawPatterns);
//                        URI remoteUri = MRequestUtils.sendRequest(requestUri, getRemoteUriRequest, URI.class, RequestMethod.POST);
//                        logger.debug(remoteUri);
//                        if (remoteUri != null) {
//                            // redirect to remote uri with parameters in json style
//                            try {
//                                return MRequestUtils.sendRequest(remoteUri, JSONObject.stringToValue(paramJsonStr), Class.forName(returnTypeStr), RequestMethod.GET);
//                            } catch (ClassNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private NacosDiscoveryProperties discoveryProperties;
    private NacosServiceManager serviceManager;
    private NacosServiceDiscovery serviceDiscovery;
    public static Object restRequest(String mObjectId, String functionName, String returnTypeStr, Object... args) throws NacosException {
        List<String> paramNameList = new ArrayList<>(args.length / 2);
        List<Object> paramValueList = new ArrayList<>(args.length / 2);
        for (int i = 0; i < args.length; i += 2) {
            paramNameList.add((String)args[i]);
            paramValueList.add(args[i+1]);
        }
        String paramJsonStr = RequestUtils.methodParamToJsonString(paramNameList, paramValueList);

        if (MClientSkeleton.getInstance().discoveryProperties != null) {
            List<ServiceInstance> instances = MClientSkeleton.getInstance().serviceDiscovery.getInstances("MClusterAgent");
            if (instances.size() > 0) {
                // request MClusterAgent for remote uri
                URI requestUri = MUrlUtils.getMClientRequestRemoteUri(instances.get(0).getHost(), instances.get(0).getPort());
                logger.debug(requestUri);
                if (requestUri != null) {
                    String rawPatterns = null;
                    Map<RequestMappingInfo, HandlerMethod> mapping = MClientSkeleton.getInstance().requestMappingHandlerMapping.getHandlerMethods();
                    for (RequestMappingInfo mappingInfo : mapping.keySet()) {
                        if (mapping.get(mappingInfo).getMethod().getName().equals(functionName)) {
                            rawPatterns = mappingInfo.getPatternsCondition().toString();
                            break;
                        }
                    }
                    MGetRemoteUriRequest getRemoteUriRequest = new MGetRemoteUriRequest();
                    getRemoteUriRequest.setFunctionName(functionName);
                    getRemoteUriRequest.setObjectId(mObjectId);
                    getRemoteUriRequest.setRawPatterns(rawPatterns);
                    URI remoteUri = MRequestUtils.sendRequest(requestUri, getRemoteUriRequest, URI.class, RequestMethod.POST);
                    logger.debug(remoteUri);
                    if (remoteUri != null) {
                        // redirect to remote uri with parameters in json style
                        try {
                            return MRequestUtils.sendRequest(remoteUri, JSONObject.stringToValue(paramJsonStr), Class.forName(returnTypeStr), RequestMethod.GET);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * request the information that needed by rest request for remote call
     * @param mObjectId: the id of MObject
     * @param functionName: the function will be used/called
     * @return String
     */
    public String getRestInfo(String mObjectId, String functionName) {
        if (!this.checkIfHasRestInfo(mObjectId, functionName)) {
            throw new RuntimeException("Failed to fetch remote url for " + functionName + " in " + mObjectId);
        }
        return this.restInfoMap.get(mObjectId).get(functionName).getRestAddress();
    }

    /**
     * check whether need to use remote call or not
     * @param mObjectId: the id of MObject
     * @param functionName: the function will be used/called
     * @return boolean
     */
    private boolean checkIfHasRestInfo(String mObjectId, String functionName) {
        return this.restInfoMap.containsKey(mObjectId) && this.restInfoMap.get(mObjectId).containsKey(functionName);
    }

    public void registerObjectAndApi(String mObjectId, String apiName) {
        if (!this.objectId2ApiSet.containsKey(mObjectId)) {
            this.objectId2ApiSet.put(mObjectId, new HashSet<>());
        }
        this.objectId2ApiSet.get(mObjectId).add(apiName);
    }
    /**
     * 获取IP地址
     *
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        Integer port = 0;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();

            }
        } catch (Exception e) {
            logger.error("Instance IP GETS ERROR ", e);
        }

        //使用代理，则获取第一个IP地址
        if(StringUtils.isEmpty(ip) && ip.length() > 15) {
            if(ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }

        return ip;
    }
}
