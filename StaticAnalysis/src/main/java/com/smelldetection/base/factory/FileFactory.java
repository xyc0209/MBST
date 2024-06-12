package com.smelldetection.base.factory;

import com.smelldetection.base.context.ServiceContext;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileFactory {

    public List<String> getPomFiles(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> pomFiles = new ArrayList<>();
        int maxDepth = 10;
        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> String.valueOf(filepath).contains("pom.xml"));

        pomFiles = stream.sorted().map(String::valueOf).filter(filepath ->{
            if(String.valueOf(filepath).toLowerCase().contains(".mvn") || String.valueOf(filepath).toLowerCase().contains("gradle")){
                return false;
            }
            else {
                return true;
            }
        }).collect(Collectors.toList());
        return  pomFiles;
    }

    public List<String> getApplicationYamlOrPropertities(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> applicationYamlOrProperities = new ArrayList<>();
        int maxDepth = 10;
        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> true);
        applicationYamlOrProperities = stream.sorted().map(String::valueOf).filter(filepath ->{
            if((String.valueOf(filepath).toLowerCase().endsWith("application.yml") || String.valueOf(filepath).toLowerCase().endsWith("application.yaml") || String.valueOf(filepath).toLowerCase().endsWith("application.properties") || String.valueOf(filepath).toLowerCase().endsWith("bootstrap.yml")) && !String.valueOf(filepath).toLowerCase().contains("target") && !String.valueOf(filepath).toLowerCase().contains("test")){
                return true;
            }
            else {
                return false;
            }
        }).collect(Collectors.toList());
        return  applicationYamlOrProperities;
    }

    public boolean isControllerFileExists(String servicesDirectory) throws IOException {
        Path start = Paths.get(servicesDirectory);
        int maxDepth = 10;
        Stream<Path> stream = Files.find(start, maxDepth, (filePath, attributes) -> true);

        return stream
                .map(Path::getFileName)
                .map(Path::toString)
                .anyMatch(fileName -> { return fileName.toLowerCase().contains("controller") || fileName.toLowerCase().contains("web");});
    }

    public List<String> getStaticFiles(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> staticFileList = new ArrayList<>();
        int maxDepth = 15;
        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> (String.valueOf(filepath).contains("html") ||String.valueOf(filepath).contains("js")));

        staticFileList = stream.sorted().map(String::valueOf).filter(filepath ->{
            if(String.valueOf(filepath).contains("\\resources\\") || String.valueOf(filepath).contains("/resources/")){
                return true;
            }
            else{
                return false;
            }
        }).collect(Collectors.toList());
        return  staticFileList;
    }
    public List<String> getJarFiles(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> jarFiles = new ArrayList<>();
        int maxDepth = 10;
        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> String.valueOf(filepath).contains(".jar"));

        jarFiles = stream.sorted().map(String::valueOf).filter(filepath ->{
            if(String.valueOf(filepath).toLowerCase().contains(".jar.original") || String.valueOf(filepath).toLowerCase().contains("wrapper")){
                return false;
            }
            else {
                return true;
            }
        }).collect(Collectors.toList());
        return  jarFiles;
    }

    public List<String> getJavaFiles(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> javaFiles;
        int maxDepth = 15;
        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> String.valueOf(filepath).endsWith(".java"));
        //ignore .java files in package test,but classes outside this package can have "test" or "Test" in their names
        javaFiles= stream.sorted().map(String::valueOf)
                .filter(filepath ->
                            (!String.valueOf(filepath).contains("\\test\\") && !String.valueOf(filepath).contains("/test/"))
                    )
                .collect(Collectors.toList());
        return javaFiles;
    }

    public String getPackageName(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> javaFiles;
        int maxDepth = 10;
        String packageName = "";
        List<String> javaFilePaths = Files.find(start, maxDepth, (filepath, attributes) -> String.valueOf(filepath).endsWith("Application.java"))
                .map(Path::toString)
                .collect(Collectors.toList());
        for(String filepath : javaFilePaths){
            if(filepath.contains("/src/main/java/"))
                packageName = filepath.substring(filepath.indexOf("java/") + 5, filepath.lastIndexOf("/")).replace('/','.');
                break;
        }
        return packageName;
    }

//    public List<String> getJavaFiles(String servicesDirectory) throws IOException {
//        Path start= Paths.get(servicesDirectory);
//        List<String> javaFiles = new ArrayList<>();
//        int maxDepth = 10;
//        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> String.valueOf(filepath).contains(".java"));
//        javaFiles = stream.sorted().map(String::valueOf).collect(Collectors.toList());
//        return  javaFiles;
//    }

    public List<String> getServicePaths(String servicesDirectory) throws IOException {
        File dir = new File(servicesDirectory);
        File[] files = dir.listFiles();
        List<String> servicesPath = new ArrayList<>();
        for(File file: files){
            if(file.isDirectory()){
                servicesPath.add(file.getAbsolutePath());
            }

        }
        return  servicesPath;
    }

    public  String getServiceName(String servicePath) throws IOException {
        FileFactory fileFactory = new FileFactory();
        List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(servicePath);
        String serviceName = null;
        for (String app : applicationYamlOrPropertities) {
            if (app.endsWith("yaml") || app.endsWith("yml") ) {
                Yaml yaml = new Yaml();
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
        return serviceName;
    }
    public List<String> getServicesCount(String servicesDirectory) throws IOException {
        File dir = new File(servicesDirectory);
        File[] files = dir.listFiles();
        List<String> servicesPath = new ArrayList<>();
        for(File file: files){
            if(file.isDirectory() && getApplicationYamlOrPropertities(file.getAbsolutePath()).size() != 0){
                servicesPath.add(file.getAbsolutePath());
            }
        }
        return  servicesPath;
    }

    public ServiceContext getServiceList(String serviceDirectory) throws IOException {

        ServiceContext serviceContext = new ServiceContext();
        String servicesDirectory = new File(serviceDirectory).getAbsolutePath();
        List<String> servicesPath = this.getServicePaths(servicesDirectory);
        for (String svc : servicesPath) {
            List<String> applicationYamlOrPropertities = this.getApplicationYamlOrPropertities(svc);
            Yaml yaml = new Yaml();
            String serviceName = "";
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
            serviceContext.getServiceList().add(serviceName);
        }
        return serviceContext;
    }
}
