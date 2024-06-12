package com.smelldetection.service;

import com.smelldetection.base.context.UnusedContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.item.UnusedType;
import com.smelldetection.base.utils.ApiParserUtils;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-01-10 22:13
 */
@Service
@NoArgsConstructor
public class UnusedService {
    @Autowired
    public FileFactory fileFactory;

    public UnusedContext getUnusedInterfaceAndAbstractClass(RequestItem request) throws IOException {
        UnusedContext unusedContext = new UnusedContext();

        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> servicesPath = fileFactory.getServicePaths(servicesDirectory);
        ApiParserUtils apiParserUtils = new ApiParserUtils();
        Map<String, Map<UnusedType, Map<String, Boolean>>> unusedMap = new HashMap<>();
        ApiParserUtils.abstractCount = 0;
        ApiParserUtils.interfaceCount = 0;
        for (String svc : servicesPath) {
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(svc);
            Yaml yaml = new Yaml();
            String serviceName = "";
            Set<String> entitySet = new HashSet<>();
            if(applicationYamlOrPropertities.size() == 0)
                continue;
            for (String app : applicationYamlOrPropertities) {
                if (app.endsWith("yaml") || app.endsWith("yml")) {
                    Map map = yaml.load(new FileInputStream(app));
                    Map m1 = (Map) map.get("spring");
                    Map m2 = (Map) m1.get("application");
                    serviceName = (String) m2.get("name");
                } else {
                    InputStream in = new BufferedInputStream(new FileInputStream(app));
                    Properties p = new Properties();
                    p.load(in);
                    serviceName = (String) p.get("spring.application.name");
                }
            }
            List<String> javaFiles = fileFactory.getJavaFiles(svc);
            Map<UnusedType, Map<String, Boolean>> methodMap =new HashMap<>();

            for (String javafile : javaFiles) {
                File file = new File(javafile);
                apiParserUtils.getInterfaceAndAbstractClass(file,methodMap,serviceName);
            }
            if(!methodMap.isEmpty()){
                unusedMap.put(serviceName, methodMap);
            }
        }
        unusedContext.setAbstractCount(ApiParserUtils.abstractCount);
        unusedContext.setInterfaceCount(ApiParserUtils.interfaceCount);
        for(String svc: servicesPath){
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(svc);
            Yaml yaml = new Yaml();
            String serviceName = "";
            Set<String> entitySet = new HashSet<>();
            for (String app : applicationYamlOrPropertities) {
                if (app.endsWith("yaml") || app.endsWith("yml")) {
                    Map map = yaml.load(new FileInputStream(app));
                    Map m1 = (Map) map.get("spring");
                    Map m2 = (Map) m1.get("application");
                    serviceName = (String) m2.get("name");
                } else {
                    InputStream in = new BufferedInputStream(new FileInputStream(app));
                    Properties p = new Properties();
                    p.load(in);
                    serviceName = (String) p.get("spring.application.name");
                }
            }
            List<String> javaFiles = fileFactory.getJavaFiles(svc);
            for (String javafile : javaFiles) {
                File file = new File(javafile);
                apiParserUtils.findMethodCall(file, unusedMap);
            }
            if(unusedMap.containsKey(serviceName)) {
                for (UnusedType unusedType : unusedMap.get(serviceName).keySet()) {
                    int sum = unusedMap.get(serviceName).get(unusedType).size();
                    int count = 0;
                    for (Boolean used : unusedMap.get(serviceName).get(unusedType).values()) {
                        if (!used) {
                            count++;
                        }
                    }
                    if (!this.isFullyUsed(unusedType.getName(),sum, count)) {
                        if (unusedType.isInterface()) {

                            if (unusedContext.getNotFullyUsedInterface().containsKey(serviceName)) {
                                unusedContext.getNotFullyUsedInterface().get(serviceName).add(unusedType.getName());
                            } else {
                                unusedContext.getNotFullyUsedInterface().put(serviceName, new HashSet<>());
                                unusedContext.getNotFullyUsedInterface().get(serviceName).add(unusedType.getName());
                            }
                        }
                        else{
                            if (unusedContext.getNotFullyUsedAbstract().containsKey(serviceName)) {
                                unusedContext.getNotFullyUsedAbstract().get(serviceName).add(unusedType.getName());
                            } else {
                                unusedContext.getNotFullyUsedAbstract().put(serviceName, new HashSet<>());
                                unusedContext.getNotFullyUsedAbstract().get(serviceName).add(unusedType.getName());
                            }

                        }
                    }
                }
            }

        }
        if(!unusedContext.getNotFullyUsedAbstract().isEmpty() || !unusedContext.getNotFullyUsedInterface().isEmpty())
            unusedContext.setStatus(true);
        return unusedContext;
    }

    public boolean isFullyUsed(String name, int sum, int count){
        if(sum == 0 && name.toLowerCase().endsWith("repository"))
            return true;
        else return (3 * count >=sum) ? false : true;
    }
}
