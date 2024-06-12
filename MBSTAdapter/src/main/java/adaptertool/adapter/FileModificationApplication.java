package adaptertool.adapter;



import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileModificationApplication {
    public static void main(String[] args) throws XmlPullParserException, IOException {
        String projectPath = "D:\\code\\train-ticket-test-main\\adapter\\test1";
        List<File> javaFiles = FileScanner.scanJavaFiles(projectPath);
        List<File> pomFiles = FileScanner.scanPomFiles(projectPath);
        FileModifier.modifyJavaFiles(javaFiles);
        FileModifier.modifyPomFiles(pomFiles);
        System.out.println("---");
    }
}