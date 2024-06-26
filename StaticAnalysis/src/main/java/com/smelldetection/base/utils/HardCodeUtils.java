package com.smelldetection.base.utils;


import com.smelldetection.base.context.HardCodeContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.FileAnalysisItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * author: yang
 * 对某个文件路径下的所有文件进行扫描。判断主流的编程文件是否包含硬编码情况
 */
public class HardCodeUtils {

    private static String regex = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(:\\d{0,5})";

    private static String regex2 = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)";
    private static String regex3 ="localhost:\\d+";
    private static Logger logger = LogManager.getLogger(HardCodeUtils.class);



    /**
     * @param path  待分析的文件路径
     * @param fileType  要分析的文件类型列表
     * @param excludeDir  不分析的文件夹名称列表
     * @return  返回分析结果
     */
    public static HardCodeContext analysisAllFiles(String path, List<String> fileType, List<String> excludeDir) throws IOException {
        long cur =System.currentTimeMillis();
        FileFactory fileFactory = new FileFactory();
        List<String> resultList = new LinkedList<>();
        List<String> servicePath = fileFactory.getServicePaths(path);
        HardCodeContext hcAnalysisResult = new HardCodeContext();
        for(String service: servicePath) {
//            if(fileFactory.getApplicationYamlOrPropertities(service).size() != 0)
            List<String> result = getJavaFiles(service);
            String serviceName = fileFactory.getServiceName(service);
            hcAnalysisResult = checkAllFile(hcAnalysisResult, serviceName,result);
            
        }
        if(!hcAnalysisResult.getAnalysisResult().isEmpty())
            hcAnalysisResult.setStatus(true);
        cur -=System.currentTimeMillis();
        System.out.println("cur"+cur);
        return hcAnalysisResult;
    }

    //获取文件夹下所有的文件路径,excludeDir是要跳过查询的文件夹名列表

    public static  List<String> getJavaFiles(String servicesDirectory) throws IOException {
        Path start= Paths.get(servicesDirectory);
        List<String> javaFiles;
        int maxDepth = 10;
        Stream<Path> stream = Files.find(start,maxDepth,(filepath, attributes) -> String.valueOf(filepath).endsWith(".java"));
        //ignore .java files in package test,but classes outside this package can have "test" or "Test" in their names
        javaFiles= stream.sorted().map(String::valueOf)
                .filter(filepath -> !String.valueOf(filepath).contains("\\test\\")&&!String.valueOf(filepath).contains("/test/"))
                .collect(Collectors.toList());
        return javaFiles;
    }

    /**
     * @param path 某文件夹下的路径
     * @param fileType 要查找的文件类型
     * @param excludeDir
     * @param resultList
     * @return
     */
    private static List<String> getFilesPath(String path,List<String> fileType,List<String> excludeDir,List<String> resultList){

        File file = new File(path);
        File[] filesList = file.listFiles();
        if (null != filesList){
            for (int i=0;i<filesList.length;i++){
                String filePath = filesList[i].toString();
//                logger.info("======="+filePath);
                if (filesList[i].isFile()){
                    int index = filePath.lastIndexOf(".");
                    if(null!=fileType && !fileType.isEmpty() && fileType.contains(filePath.substring(index+1))){
                        resultList.add(filePath);
                    }
                }
                else {
                    //文件夹处理
                    if (excludeDir.isEmpty()||null==excludeDir){
                        getFilesPath(filePath,fileType,null,resultList);
                    }
                    else {
                        String dirName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
//                        logger.info("++++++++++++++++"+dirName);
                        if (!excludeDir.contains(dirName)) {
                            getFilesPath(filePath, fileType, excludeDir,resultList);
                        }

                    }

                }
            }
        }
        return resultList;
    }

    /**
     * @param pathList 文件路径列表
     * @return
     */
    private static HardCodeContext checkAllFile(HardCodeContext hcAnalysisResult, String serviceName, List<String> pathList){
        List<FileAnalysisItem> tempResult;
        for (int i = 0; i < pathList.size(); i++) {
            tempResult = isContainHardCode(pathList.get(i));

            for (int j = 0; j < tempResult.size(); j++) {
                hcAnalysisResult.add(serviceName, tempResult.get(j));
            }
        }
        return hcAnalysisResult;
    }

    /**
     * @param path 文件路径
     * @return  该文件内容中是否包含ip：port形式的硬编码
     */
    private static List<FileAnalysisItem> isContainHardCode(String path){
        List<FileAnalysisItem> fileAnalysisResultList = new LinkedList<>();
        File file = new File(path);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readline = null;
            int row = 0;
            while ((readline = bufferedReader.readLine())!=null){
                row++;
                if(readline.trim().startsWith("//"))
                    continue;
                Matcher matcher = Pattern.compile(regex).matcher(readline);
                Matcher matcher2 = Pattern.compile(regex2).matcher(readline);
                Matcher matcher3 = Pattern.compile(regex3).matcher(readline);
                if (matcher.find()){
                    FileAnalysisItem fileAnalysisResult = new FileAnalysisItem();
                    fileAnalysisResult.setHardCode(matcher.group());
                    fileAnalysisResult.setFileName(path.substring(path.lastIndexOf(File.separator)+1));
                    fileAnalysisResult.setFilePath(path.replace("\\", "/"));
                    fileAnalysisResult.setPlace(row);
                    fileAnalysisResultList.add(fileAnalysisResult);
                }
                else if(matcher2.find()){
                    FileAnalysisItem fileAnalysisResult = new FileAnalysisItem();
                    fileAnalysisResult.setHardCode(matcher2.group());
                    fileAnalysisResult.setFileName(path.substring(path.lastIndexOf(File.separator)+1));
                    fileAnalysisResult.setFilePath(path.replace("\\", "/"));
                    fileAnalysisResult.setPlace(row);
                    fileAnalysisResultList.add(fileAnalysisResult);
                }
                else if(matcher3.find()){
                    FileAnalysisItem fileAnalysisResult = new FileAnalysisItem();
                    fileAnalysisResult.setHardCode(matcher3.group());
                    fileAnalysisResult.setFileName(path.substring(path.lastIndexOf(File.separator)+1));
                    fileAnalysisResult.setFilePath(path.replace("\\", "/"));
                    fileAnalysisResult.setPlace(row);
                    fileAnalysisResultList.add(fileAnalysisResult);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return fileAnalysisResultList;
    }

    public static void main(String[] args) {

    }

}
