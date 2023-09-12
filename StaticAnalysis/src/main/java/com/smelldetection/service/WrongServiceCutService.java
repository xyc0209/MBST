package com.smelldetection.service;

import com.smelldetection.base.context.WrongCutContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
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
 * @date: 2022-12-26 21:02
 */

@Service
@NoArgsConstructor
public class WrongServiceCutService {

    @Autowired
    public FileFactory fileFactory;

    public WrongCutContext getServicesEntityCount(RequestItem request) throws IOException {
        WrongCutContext wrongCutContext = new WrongCutContext();
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> servicesPath = fileFactory.getServicePaths(servicesDirectory);
        ApiParserUtils apiParserUtils = new ApiParserUtils();
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
            for (String javafile : javaFiles) {
                File file = new File(javafile);
                if(javafile.toLowerCase().contains("/entity/") || javafile.toLowerCase().contains("/domain/")){
                    entitySet.add(javafile);
                }
                Set<String> count = new HashSet<>();
                if (apiParserUtils.isEntityClass(file, count, serviceName) == 1){
                    entitySet.add(javafile);
                }
            }
            if(!serviceName.equals("")){
                Map<String, Integer> map = new HashMap<>();
                if(entitySet.size() != 0){
                    map.put("entityCount",entitySet.size());
                    wrongCutContext.getWrongCutMap().put(serviceName,map);
                }

            }
        }
        return  wrongCutContext;
    }

    public double getAvgEntityCount(WrongCutContext wrongCutContext){
        double avgEntityCount = 0;
        int size = wrongCutContext.getWrongCutMap().size();
        for (String svc : wrongCutContext.getWrongCutMap().keySet()) {
            avgEntityCount += wrongCutContext.getWrongCutMap().get(svc).get("entityCount").intValue();
        }
        avgEntityCount = avgEntityCount / size;
        return avgEntityCount;

    }
    public WrongCutContext getWrongServiceCutServices(RequestItem request) throws IOException {
        WrongCutContext wrongCutContext= this.getServicesEntityCount(request);
        int size = wrongCutContext.getWrongCutMap().size();
        double avgEntityCount = this.getAvgEntityCount(wrongCutContext);
        double quadraticSum = 0.0;
        for (String svc : wrongCutContext.getWrongCutMap().keySet()) {
            quadraticSum += Math.pow(wrongCutContext.getWrongCutMap().get(svc).get("entityCount").intValue() - avgEntityCount, 2);
        }
        double std = Math.sqrt(quadraticSum / size);
        WrongCutContext wrongCutResult = new WrongCutContext();
        for (String svc : wrongCutContext.getWrongCutMap().keySet()) {
            int entityCount = wrongCutContext.getWrongCutMap().get(svc).get("entityCount").intValue();
            if(Math.abs(entityCount - avgEntityCount) >= 3* std && (size != 1) && std != 0) {
                if(entityCount > avgEntityCount && entityCount <= 2)
                    continue;
                wrongCutResult.getWrongCutMap().put(svc,wrongCutContext.getWrongCutMap().get(svc));
            }
        }
        if(!wrongCutResult.getWrongCutMap().isEmpty())
            wrongCutResult.setStatus(true);

        return wrongCutResult;
    }


}
