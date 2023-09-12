package com.smelldetection.service;

import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.context.SharedDatabaseContext;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-21 12:31
 */
@Service
@NoArgsConstructor
@SuppressWarnings("rawtypes")
public class SharedDatabaseAndServiceIntimacyService {

    @Autowired
    public FileFactory fileFactory;

    /**
    * @Description: collect data of shareDatabase and serviceIntimacy
    * @Param: [request]
    * @return: com.example.smelldetection.base.context.SharedDatabaseContext
    */
    public SharedDatabaseContext getsharedDatabaseandServiceIntimacy(RequestItem request) throws IOException {
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> applicationYamlOrProperties= fileFactory.getApplicationYamlOrPropertities(servicesDirectory);
        Yaml yaml = new Yaml();
        HashMap<String,ArrayList<String>> databaseMap = new HashMap<>();
        HashMap<String,ArrayList<String>> ServiceIntimacyMap = new HashMap<>();
        SharedDatabaseContext sharedDatabaseContext = new SharedDatabaseContext();
        for(String app: applicationYamlOrProperties){
            String serviceName = "";
            if(app.endsWith("yaml") || app.endsWith("yml")){
                Map map = yaml.load(new FileInputStream(app));
                Map m1 =(Map)map.get("spring");
                Map m2 = (Map)m1.get("application");
                serviceName = (String)m2.get("name");
            }
            else{
                InputStream in = new BufferedInputStream(new FileInputStream(app));
                Properties p = new Properties();
                p.load(in);
                serviceName = (String)p.get("spring.application.name");
            }
            BufferedReader reader = new BufferedReader(new FileReader(app));
            String line = reader.readLine();
            String pattern = "mysql://";
            String target = "";
            int row = 0;
            while (line != null) {
                if (line.contains(pattern)) {
                    int startIndex = line.indexOf(pattern) + 8;
                    int endIndex = line.indexOf("?");
                    if (line.contains("///")) {
                        startIndex = line.indexOf("///") + 3;
                        target = "localhost:3306/" + line.substring(startIndex, endIndex);
                    }
                    else if(line.contains("127.0.0.1")){
                        startIndex = line.indexOf("//")+2;
                        target = line.substring(startIndex, endIndex);
                        target = target.replace("localhost","127.0.0.1");
                    }
                    else {
                        target = line.substring(startIndex, endIndex);
                    }
                    if (databaseMap.containsKey(target)) {
                        databaseMap.get(target).add(serviceName);
                    } else {
                        databaseMap.put(target, new ArrayList<>());
                        databaseMap.get(target).add(serviceName);
                    }
                    if (ServiceIntimacyMap.containsKey(serviceName)) {
                        ServiceIntimacyMap.get(serviceName).add(target);
                    } else {
                        ServiceIntimacyMap.put(serviceName, new ArrayList<>());
                        ServiceIntimacyMap.get(serviceName).add(target);
                    }
                }
                line = reader.readLine();
            }
        }
        Set<String> keys = databaseMap.keySet();
        for(String key:keys){
            ArrayList<String> servicesList=databaseMap.get(key);
            if(servicesList.size()>1) {
                boolean shared = true;
                for (String service : servicesList) {
                    if (ServiceIntimacyMap.get(service).size() > 1) {
                        shared = false;
                    }
                    break;
                }
                if(shared){
                    sharedDatabaseContext.addSharedDatabase(key,servicesList);
                }
                else sharedDatabaseContext.addServiceIntimacy(key,servicesList);
            }
        }
        return sharedDatabaseContext;
    }
}
