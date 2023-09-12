package com.mbs.mclient.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

import com.mbs.mclient.base.DatabaseLog;
import com.mbs.mclient.base.Operate;
import com.mbs.common.utils.LogstashUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

//    private static  Logger logger = (Logger) LoggerFactory.getLogger("SQL");
//    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");

    private static final String LOG_DIR = "logs";
    private static final String LogstashIp = "172.16.17.38";
    private static final int LogstashPort = 32001;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment env;



    @Around("@annotation(com.mbs.mclient.annotation.Loggable)")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        this.configure();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("SQL");
        Logger LOGGER = loggerContext.getLogger("org.hibernate.SQL");

        String threadName = Thread.currentThread().getName();
        System.out.println("Current thread name: " + threadName);
        // create MemoryAppender
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setName("hibernate"); // 替换为实际的 Appender 名称
        appender.setContext(loggerContext);
        appender.start();

        // add MemoryAppender to Logger
        Logger classicLogger = LOGGER;
        classicLogger.setLevel(Level.ALL);
        classicLogger.addAppender(appender);

        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String dataAccessFramework = getDataAccessFramework();
        if(dataAccessFramework.equals("Spring Data JPA")){

        }
        DatabaseLog databaseLog = new DatabaseLog();
        System.out.println("DataAccessFramework is  {}"+ dataAccessFramework);
        String service = "";
        String databaseUrl= "";
        String secondaryDatavaseUrl="";
        try {
             service = env.getProperty("spring.application.name");
             databaseUrl = env.getProperty("spring.datasource.url");
             secondaryDatavaseUrl = env.getProperty("spring.datasource.secondary.url");
        }
        catch ( NullPointerException e){
            System.out.println("Missing configuration information");
        }
        String[] str =databaseUrl.substring(databaseUrl.lastIndexOf("//")+2, databaseUrl.lastIndexOf("?")).split("/");
        for(String s:str)
            System.out.println(s);
        databaseLog.setFramework(dataAccessFramework);
        databaseLog.setServiceName(service);
        databaseLog.setDatabaseUrl(str[0]);
        databaseLog.setDatabaseName(str[1]);
        if(secondaryDatavaseUrl != null){
            String[] str2 =secondaryDatavaseUrl.substring(secondaryDatavaseUrl.lastIndexOf("//")+2, secondaryDatavaseUrl.lastIndexOf("?")).split("/");
            databaseLog.setSecondaryDatabaseUrl(str2[0]);
            databaseLog.setSecondaryDatabaseName(str2[1]);
        }


        Object result = joinPoint.proceed();
        System.out.println("size"+appender.list.size());
        String[] sqlstr = new String[0];
        for (ILoggingEvent event : appender.list) {
            System.out.println("event--"+event.getFormattedMessage());
        }
        for (ILoggingEvent event : appender.list) {
            System.out.println("log thread"+event.getThreadName());
            if(event.getFormattedMessage().contains("select") || event.getFormattedMessage().contains("delete") ||event.getFormattedMessage().contains("update") ||
                    event.getFormattedMessage().contains("insert")) {
                sqlstr = event.getFormattedMessage().trim().split("\\s+");
                System.out.println("sql"+sqlstr[0]);
                ArrayList<String> strings = new ArrayList<>(Arrays.asList(sqlstr));
                databaseLog.setOperate(Operate.valueOf(sqlstr[0]));
                if(sqlstr[0].equals("select") || sqlstr[0].equals("delete"))
                    databaseLog.setTable(strings.get(strings.indexOf("from") + 1));
                else if (sqlstr[0].equals("update")) {
                    databaseLog.setTable(strings.get(strings.indexOf("update") + 1));
                } else{
                    databaseLog.setTable(strings.get(strings.indexOf("into") + 1));
                }
                databaseLog.setCurrentTimeMillis(System.currentTimeMillis());
                System.out.println(databaseLog.toString());
                logger.info(databaseLog.toString());
                if(secondaryDatavaseUrl == null)
                    LogstashUtils.sendInfoToLogstash(LogstashIp, LogstashPort, convertLog2JsonObejct(databaseLog).toString());
                else
                    LogstashUtils.sendInfoToLogstash(LogstashIp, LogstashPort, convertTwoSourcesLog2JsonObejct(databaseLog).toString());
                System.out.println("databaseLog"+convertLog2JsonObejct(databaseLog).toString());
            }
        }
//        databaseLog.setCurrentTimeMillis(System.currentTimeMillis());
//        System.out.println(databaseLog.toString());
//        logger.info(databaseLog.toString());
//        LogstashUtils.sendInfoToLogstash("172.16.17.38", 32001, convertLog2JsonObejct(databaseLog).toString());
        System.out.println(logger.getName());
        // remove output of MemoryAppender
        appender.list.clear();
        return result;
    }
    public static JSONObject convertLog2JsonObejct(DatabaseLog baseLog) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sqlLog", baseLog.toJson());
        return jsonObject;
    }
    public static JSONObject convertTwoSourcesLog2JsonObejct(DatabaseLog baseLog) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sqlLog", baseLog.twoSourcesToJson());
        return jsonObject;
    }
    public static void configure() {


        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // crreate hibernate  appender
        RollingFileAppender hibernateAppender = new RollingFileAppender();
        hibernateAppender.setName("hibernate");
        //hibernateAppender.setFile("E:/doctor/myservice/logs/hibernate.log");
        hibernateAppender.setContext(context);
        // create  rolling policy of hibernate
        SizeAndTimeBasedRollingPolicy hibernateRollingPolicy = new SizeAndTimeBasedRollingPolicy();
        hibernateRollingPolicy.setFileNamePattern("logs/hibernate.%d{yyyy-MM-dd}.%i.log");
        hibernateRollingPolicy.setMaxFileSize(FileSize.valueOf("10MB"));
        hibernateRollingPolicy.setMaxHistory(10);
        hibernateRollingPolicy.setTotalSizeCap(FileSize.valueOf("1GB"));
        hibernateRollingPolicy.setParent(hibernateAppender);
        hibernateRollingPolicy.setContext(context);
        hibernateRollingPolicy.start();

        // set rolling policy to appender
        hibernateAppender.setRollingPolicy(hibernateRollingPolicy);

        // create encoder of hibernate
        PatternLayoutEncoder hibernateEncoder = new PatternLayoutEncoder();
        hibernateEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        hibernateEncoder.setContext(context);
        hibernateEncoder.start();

        // set encoder and rolling policy to appender
        hibernateAppender.setEncoder(hibernateEncoder);
        hibernateAppender.start();

        // create hibernate logger
        Logger hibernateLogger = context.getLogger("org.hibernate.SQL");
        hibernateLogger.setLevel(Level.DEBUG);
        hibernateLogger.setAdditive(false);

        // add appender to logger
        hibernateLogger.addAppender(hibernateAppender);

        //create sql appender
        RollingFileAppender sqlAppender = new RollingFileAppender();
        sqlAppender.setName("sql");
        sqlAppender.setContext(context);

        //create sql rolling policy
        SizeAndTimeBasedRollingPolicy sqlRollingPolicy = new SizeAndTimeBasedRollingPolicy();
        sqlRollingPolicy.setFileNamePattern("logs/sql.%d{yyyy-MM-dd}.%i.log");
        sqlRollingPolicy.setMaxFileSize(FileSize.valueOf("10MB"));
        sqlRollingPolicy.setMaxHistory(10);
        sqlRollingPolicy.setTotalSizeCap(FileSize.valueOf("1GB"));
        sqlRollingPolicy.setParent(sqlAppender);
        sqlRollingPolicy.setContext(context);
        sqlRollingPolicy.start();

        // set rolling policy to appender
        sqlAppender.setRollingPolicy(sqlRollingPolicy);

        // create sql encoder
        PatternLayoutEncoder sqlEncoder = new PatternLayoutEncoder();
        sqlEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        sqlEncoder.setContext(context);
        sqlEncoder.start();

        // set encoder and rolling policy to appender
        sqlAppender.setEncoder(sqlEncoder);
        sqlAppender.start();

        // create sql logger
        Logger sqlLogger = context.getLogger("SQL");
        sqlLogger.setLevel(Level.DEBUG);
        sqlLogger.setAdditive(false);

        // add appender to logger
        sqlLogger.addAppender(sqlAppender);

        // create  org.hibernate.type.descriptor.sql logger
        Logger hibernateTypeDescriptorSqlLogger = context.getLogger("org.hibernate.type.descriptor.sql");
        hibernateTypeDescriptorSqlLogger.setLevel(Level.TRACE);
        hibernateTypeDescriptorSqlLogger.setAdditive(false);

        // add hibernate appender to org.hibernate.type.descriptor.sql  logger
        hibernateTypeDescriptorSqlLogger.addAppender(hibernateAppender);


    }

    private static PatternLayoutEncoder getEncoder() {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.setContext((Context) LoggerFactory.getILoggerFactory());
        encoder.start();
        return encoder;
    }


    private String getDataAccessFramework() {
        if (applicationContext.containsBean("entityManagerFactory")) {
            return "Spring Data JPA";
        } else if (applicationContext.containsBean("sqlSessionFactory")) {
            return "MyBatis";
        } else {
            return "Unknown";
        }
    }

}