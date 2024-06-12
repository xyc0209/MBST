package com.smelldetection.service;

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
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2022-12-17 10:36
 */
@Service
@NoArgsConstructor
public class SharedLibraryService {
    @Autowired
    public FileFactory fileFactory;
    
    /**
    * @Description: collect data of sharedLibraries
    * @Param: [request]
    * @return: com.example.smelldetection.base.SharedLibarayContext
    */
    public SharedLibraryContext getSharedLibraries(RequestItem request) throws IOException, XmlPullParserException {
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> pomFiles= fileFactory.getPomFiles(servicesDirectory);
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        SharedLibraryContext libraryContext = new SharedLibraryContext();
        int num = pomFiles.size();
        for(int i = 0;i < num - 1; i++){
            for(int j = i +1; j<num; j++){
                Model svc1 = mavenReader.read(new FileReader(pomFiles.get(i)));
                Model svc2 = mavenReader.read(new FileReader(pomFiles.get(j)));
                for(Dependency dependency1: svc1.getDependencies()){
                    for(Dependency dependency2:svc2.getDependencies()){
                        if (dependency1.getGroupId().equals(dependency2.getGroupId()) && dependency1.getArtifactId().equals(dependency2.getArtifactId())){
                            String sharedLibrary = "";
                            if(dependency1.getVersion() != null && dependency2.getVersion() != null)
                                    sharedLibrary = dependency1.getGroupId() + "." + dependency1.getArtifactId() +"." + dependency1.getVersion();
                            else
                                sharedLibrary = dependency1.getGroupId() + "." + dependency1.getArtifactId();
                            if(sharedLibrary.startsWith("org.springframework.boot") || sharedLibrary.startsWith("org.springframework.cloud."))
                                continue;
                            String services1 = svc1.getGroupId() +"." +svc1.getArtifactId();
                            String services2 = svc2.getGroupId() +"." +svc2.getArtifactId();
                            SharedLibraryItem libraryItem= libraryContext.getSharedLibraries().getOrDefault(sharedLibrary,new SharedLibraryItem(sharedLibrary));
                            libraryItem.addSvc(services1, services2);
                            libraryContext.addItem(libraryItem);
                            break;
                        }
                    }
                }
            }
        }
        if(!libraryContext.getSharedLibraries().isEmpty())
            libraryContext.setStatus(true);
        return libraryContext;

    }
}
