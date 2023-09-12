package com.mbs.mclient.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
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
        logger.info("从配置文件读取参数：" + key+":  " + value);
        return value;
    }
    /**
     * @return 加载引用这个jar包的springboot配置文件
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream in = null;
        // 优先从项目路径获取连接信息
        String confPath = System.getProperty("user.dir");
        System.out.println("user.dir"+ confPath);
        String confPath1 = confPath + File.separator + "application.properties";
        String confPath2 = confPath + File.separator + "application.yaml";
        String confPath3 = confPath + File.separator + "application.yml";
        System.out.println("confPath3"+confPath3);
        File file1 = new File(confPath1);
        File file2 = new File(confPath2);
        File file3 = new File(confPath3);
        if (file1.exists() || file2.exists() || file3.exists()) {
            logger.info("配置文件路径 :: " + file3);
            try {
                if(file1.exists()) {
                    in = new FileInputStream(file1);
                }
                else if(file2.exists()){
                in = new FileInputStream(file2);
                }
                else{
                    in = new FileInputStream(file3);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("项目路径[" + confPath1 + "]下并无配置文件，从classpath路径下加载");

            in = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            if(in == null)
                in = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.yaml");
            if(in == null)
                in = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.yml");
            logger.info("======"+in);
        }
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}