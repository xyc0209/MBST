package com.mbs.mclient.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * used for loading properties file from web project which imports the jar
 */
public class PropertiesUtils {

    public static Logger logger = LogManager.getLogger(PropertiesUtils.class);
    /**
     * properties
     */
    private static Properties properties = null;

    /**
     * 根据key获取value值
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        if (properties == null) {
            properties = loadProperties();
        }
    for(String key1 : properties.stringPropertyNames()){
        System.out.println("key1:"+ key1+"  value:" + properties.getProperty(key1));
    }

     String value = properties.getProperty(key);
        logger.info(key+":  " + value);
        return value;
    }
    /**
     * @return 加载引用这个jar包的springboot配置文件
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream in = null;
        // getting connection information from the project path
        String confPath = System.getProperty("user.dir");
        System.out.println("user.dir"+ confPath);
        String confPath1 = confPath + File.separator  + "src" + File.separator + "main" +  File.separator + "application.properties";
        String confPath2 = confPath +  File.separator  + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "application.yaml";
        String confPath3 = confPath +  File.separator  + "src" + File.separator + "main" + File.separator + "resources" + File.separator +  "application.yml";
        System.out.println("confPath3"+confPath3);
        File file1 = new File(confPath1);
        File file2 = new File(confPath2);
        File file3 = new File(confPath3);
        if (file1.exists() || file2.exists() || file3.exists()) {
            try {
                if(file1.exists()) {
                    in = new FileInputStream(file1);
                }
                else if(file2.exists()){
                in = new FileInputStream(file2);
                }
                else{
                    System.out.println("confPath3 exist");
                    in = new FileInputStream(file3);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            List<String> fileNames = Arrays.asList("application.properties", "application.yaml", "application.yml");
            for (String fileName : fileNames) {
                in = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
                if (in != null) {
                    break;
                }
            }
        }
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}