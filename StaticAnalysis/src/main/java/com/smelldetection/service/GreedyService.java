package com.smelldetection.service;

import com.smelldetection.base.context.ApiVersionContext;
import com.smelldetection.base.context.GreedyContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.item.ServiceItem;
import com.smelldetection.base.utils.ApiParserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-13 15:31
 */
@Service
public class GreedyService {
    @Autowired
    public FileFactory fileFactory;
    @Autowired
    public WrongServiceCutService wrongServiceCutService;

    public GreedyContext getGreedySvc(RequestItem request) throws IOException {
        GreedyContext greedyContext = new GreedyContext();
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> servicesPath = fileFactory.getServicePaths(servicesDirectory);
        ApiParserUtils apiParserUtils = new ApiParserUtils();
        ApiVersionContext apiVersionContext = new ApiVersionContext();
        for (String svc : servicesPath) {
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(svc);
            Boolean isControllerFileExists = fileFactory.isControllerFileExists(svc);
            List<String> staticFiles = fileFactory.getStaticFiles(svc);
            Yaml yaml = new Yaml();
            String serviceName = "";
            if (applicationYamlOrPropertities.size() == 0)
                continue;
            for (String app : applicationYamlOrPropertities) {
                if(app.endsWith("yaml") || app.endsWith("yml")){
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
            if(!staticFiles.isEmpty() && staticFiles.size() <= 2 && isControllerFileExists){
                System.out.println("staticFiles"+staticFiles.toString());
                greedyContext.addGreedySvc(new ServiceItem(serviceName, staticFiles));
            }
            else if(staticFiles.isEmpty() && wrongServiceCutService.getServicesEntityCount(request).getWrongCutMap().containsKey(serviceName) && wrongServiceCutService.getServicesEntityCount(request).getWrongCutMap().get(serviceName).get("entityCount") <1 && isControllerFileExists)
                greedyContext.addGreedySvc(new ServiceItem(serviceName, staticFiles));
        }
        if(!greedyContext.getGreedySvc().isEmpty())
            greedyContext.setStatus(true);
        return greedyContext;
    }
}
