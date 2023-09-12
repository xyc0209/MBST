package com.smelldetection.service;

import com.smelldetection.base.context.HubContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.DependCount;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.utils.ApiParserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-13 17:35
 */
@Service
public class HubService {
    @Autowired
    public FileFactory fileFactory;
    @Autowired
    public ApiParserUtils apiParserUtils;

    public HubContext getHubClass(RequestItem request) throws IOException {
        HubContext hubContext = new HubContext();
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> servicesPath = fileFactory.getServicePaths(servicesDirectory);
        ApiParserUtils apiParserUtils = new ApiParserUtils();
        Map<String, DependCount> imOutMap = new HashMap<>();
        //get all classes' qualified names
        for (String svc : servicesPath) {
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(svc);

            Yaml yaml = new Yaml();
            String serviceName = "";
            Set<String> entitySet = new HashSet<>();
            if (applicationYamlOrPropertities.size() == 0)
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
            List<String> javaList = fileFactory.getJavaFiles(svc);
            for(String file: javaList){
                apiParserUtils.getAllQualifiedName(new File(file), imOutMap, serviceName);
            }

        }

        // to count imports and outports of per class
        for (String svc : servicesPath) {
            List<String> javaList = fileFactory.getJavaFiles(svc);
            for(String file: javaList){
                apiParserUtils.getImports(new File(file), imOutMap);
            }

        }

        for(String cla: imOutMap.keySet()){
            int out = imOutMap.get(cla).getOutputCount();
            int in = imOutMap.get(cla).getImportCount();
            String belongsService = imOutMap.get(cla).getBelongsService();
            if(out >= 10 && in >= 10 && Math.max(out, in) * 0.9 <= Math.min(out, in)){
                hubContext.getHubclass().put(belongsService, new DependCount(imOutMap.get(cla)));
            }
        }
        hubContext.setSystemClassCount(imOutMap.size());
        if(!hubContext.getHubclass().isEmpty())
            hubContext.setStatus(true);
        return hubContext;
    }
}
