package adaptertool.Controller;

import adaptertool.adapter.FileModifier;
import adaptertool.adapter.FileScanner;
import adaptertool.base.RequestItem;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("adapt/v1.0")
public class AdapterController {

    @RequestMapping(path = "adjust", method = RequestMethod.POST)
    public String getAnalysisResults(@RequestBody RequestItem requestItem) throws IOException, XmlPullParserException {
        String projectPath = requestItem.getServicesPath();
        List<File> javaFiles = FileScanner.scanJavaFiles(projectPath);
        List<File> pomFiles = FileScanner.scanPomFiles(projectPath);
        FileModifier.modifyJavaFiles(javaFiles);
        FileModifier.modifyPomFiles(pomFiles);
        return "Complete adaptation";
    }

}
