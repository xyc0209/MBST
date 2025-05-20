package adaptertool.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    public static List<File> scanJavaFiles(String rootPath) {
        List<File> javaFiles = new ArrayList();
        scanDirectory(new File(rootPath), javaFiles);
        return javaFiles;
    }

    public static List<File> scanPomFiles(String rootPath) {
        List<File> pomFiles = new ArrayList();
        scanDirectoryForPomFiles(new File(rootPath), pomFiles);
        return pomFiles;
    }
    private static void scanDirectory(File directory, List<File> javaFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file, javaFiles);
                } else if (file.getName().toLowerCase().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
    }

    private static void  scanDirectoryForPomFiles(File directory, List<File> pomFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectoryForPomFiles(file, pomFiles);
                } else if (file.getName().toLowerCase().equals("pom.xml")) {
                    pomFiles.add(file);
                }
            }
        }

    }



    private static boolean isControllerPackage(File directory) {
        String packageName = directory.getName().toLowerCase();
        return packageName.endsWith("controller");
    }

    private static void scanControllerPackage(File controllerPackage, List<File> javaFiles) {
        File[] files = controllerPackage.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanControllerPackage(file, javaFiles);
                } else if (file.getName().toLowerCase().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
    }
}