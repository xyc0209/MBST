package com.smelldetection.service;

import com.smelldetection.base.context.CBContext;
import com.smelldetection.base.context.SharedLibraryContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.item.SharedLibraryItem;
import lombok.NoArgsConstructor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: xyc
 * @date: 2025-05-15 14:44
 */
@Service
@NoArgsConstructor
public class CBService {
    @Autowired
    public FileFactory fileFactory;

    /**
     * @Description: collect data of sharedLibraries
     * @Param: [request]
     * @return: com.example.smelldetection.base.SharedLibarayContext
     */
    public CBContext getCBLibraries(RequestItem request) throws IOException, XmlPullParserException {
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> pomFiles= fileFactory.getPomFiles(servicesDirectory);
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        CBContext libraryContext = new CBContext();

        // 新增：存储所有服务的依赖信息
        List<Model> serviceModels = new ArrayList<>();
        Map<String, Boolean> hystrixUsage = new HashMap<>();  // record if use Hystrix
        Map<String, Boolean> resilience4jUsage = new HashMap<>(); // record if use Resilience4j
        Map<String, Boolean> sentinelUsage = new HashMap<>(); // record if use Sentinel

        // 第一步：解析所有POM文件并检测断路器依赖
        for (String pomFile : pomFiles) {
            Model model = mavenReader.read(new FileReader(pomFile));
            String serviceName = model.getGroupId() + "." + model.getArtifactId();
            serviceModels.add(model);

            // 初始化断路器标记
            boolean hasHystrix = false;
            boolean hasResilience4j = false;
            boolean hasSentinel = false;
            // 检测依赖项
            for (Dependency dependency : model.getDependencies()) {
                // Hystrix依赖检测
                if (isHystrixDependency(dependency)) {
                    hasHystrix = true;
                    hystrixUsage.put(serviceName, hasHystrix);
                    break;
                }
                // Resilience4j依赖检测
                if (isResilience4jDependency(dependency)) {
                    hasResilience4j = true;
                    resilience4jUsage.put(serviceName, hasResilience4j);
                    break;
                }
                // Sentinel依赖检测
                if (isSentinelyDependency(dependency)) {
                    hasSentinel = true;
                    sentinelUsage.put(serviceName, hasSentinel);
                    break;
                }

            }


        }

        // 将检测结果存入返回对象
        libraryContext.setHystrixUsage(hystrixUsage);
        libraryContext.setResilience4jUsage(resilience4jUsage);
        libraryContext.setSentinelUsage(sentinelUsage);
        if (!hystrixUsage.isEmpty() || !resilience4jUsage.isEmpty() || !sentinelUsage.isEmpty())
            libraryContext.setHasCircuitBreaker(true);

        return libraryContext;

    }


    // Hystrix依赖判断方法
    private boolean isHystrixDependency(Dependency dependency) {
        return ("org.springframework.cloud".equals(dependency.getGroupId()) &&
                "spring-cloud-starter-netflix-hystrix".equals(dependency.getArtifactId())) ||
                ("com.netflix.hystrix".equals(dependency.getGroupId()) &&
                        "hystrix-core".equals(dependency.getArtifactId()));
    }

    // Resilience4j依赖判断方法
    private boolean isResilience4jDependency(Dependency dependency) {
        return ("io.github.resilience4j".equals(dependency.getGroupId()) &&
                dependency.getArtifactId().startsWith("resilience4j-")) ||
                ("org.springframework.cloud".equals(dependency.getGroupId()) &&
                        "spring-cloud-starter-circuitbreaker-resilience4j".equals(dependency.getArtifactId()));
    }
    // Sentinely依赖判断方法
    private boolean isSentinelyDependency(Dependency dependency) {
        return ("com.alibaba.cloud".equals(dependency.getGroupId()) &&
                "spring-cloud-starter-alibaba-sentinel".equals(dependency.getArtifactId()));
    }
}