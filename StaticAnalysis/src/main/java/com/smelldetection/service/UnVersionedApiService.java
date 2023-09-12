package com.smelldetection.service;

import com.smelldetection.base.context.ApiVersionContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.utils.ApiParserUtils;
import com.github.javaparser.ParseException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-22 10:39
 */
@Service
@NoArgsConstructor
public class UnVersionedApiService {

    @Autowired
    public FileFactory fileFactory;

    public ApiVersionContext getUnVersionedApis(RequestItem request) throws IOException, ClassNotFoundException, ParseException {
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> servicesPath = fileFactory.getServicePaths(servicesDirectory);
        ApiParserUtils apiParserUtils = new ApiParserUtils();
        ApiVersionContext apiVersionContext = new ApiVersionContext();
        for (String svc : servicesPath) {
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(svc);
            Yaml yaml = new Yaml();
            String serviceName = "";
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
                apiVersionContext.getUnversionedMap().put(serviceName, new HashMap<>());
                apiVersionContext.getMissingUrlMap().put(serviceName,new HashMap<>());
            }
            List<String> javaFiles = fileFactory.getJavaFiles(svc);
            for (String javafile : javaFiles) {
                File file = new File(javafile);
                apiParserUtils.inspectJavaFile(file, apiVersionContext, serviceName);
            }
        }
        boolean status =false;
        for(String s: apiVersionContext.getUnversionedMap().keySet()) {
            if(!apiVersionContext.getUnversionedMap().get(s).isEmpty()){
                status=true;
                break;
            }

        }
        apiVersionContext.setStatus(status);

        return apiVersionContext;
    }

}
