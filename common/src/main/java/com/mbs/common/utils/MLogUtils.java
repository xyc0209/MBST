package com.mbs.common.utils;

import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.mbs.common.log.MBaseLog;
import com.mbs.common.log.MFunctionCalledLog;
import com.mbs.common.log.MLogType;
import com.mbs.common.log.MServiceBaseLog;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.classic.PatternLayout;


public class MLogUtils {
    public static Logger logger;

    static {
        try {
            logger = createLogger();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String LogstashIp = "172.16.17.38";
    private static final int LogstashPort = 32001;

    public static String convertLogObjectToString(MServiceBaseLog baseLog) {
        return baseLog.toString();
    }

    public static MBaseLog getLogObjectFromString(String formattedStr) {
        return MBaseLog.getLogFromStr(formattedStr);
    }

    private static Logger createLogger() throws IllegalAccessException {
        final LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayout layout = new PatternLayout();
        layout.setPattern("%msg%n");
        layout.setContext(context);
        layout.start();

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(context);
        encoder.setLayout(layout);
        encoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE_APPENDER");
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setName("FILE_APPENDER");
        fileAppender.setFile("/var/log/mclient/file-with-date.log");
        fileAppender.setEncoder(encoder);
        fileAppender.start();



        Logger logger = context.getLogger("MLogUtils.class");
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
        ((ch.qos.logback.classic.Logger) logger).addAppender(consoleAppender);
        ((ch.qos.logback.classic.Logger) logger).addAppender(fileAppender);
        return logger;
//        final Configuration config = ctx.getConfiguration();
//        String fileName = config.
//                getStrSubstitutor().replace("/var/log/mclient/file-with-date-${date:yyyy-MM-dd}.log");
//
//        PatternLayout layout = PatternLayout.newBuilder()
//                .withConfiguration(ctx.getConfiguration())
//                .withPattern("%m%n").build();
//
//        Appender fileAppender = FileAppender.newBuilder()
//                .setLayout(layout)
//                .withFileName(fileName)
//                .setName("pattern")
//                .build();
//        fileAppender.start();
//
//        Appender consoleAppender =  ConsoleAppender.createAppender(layout, null, null, "CONSOLE_APPENDER", null, null);
//        consoleAppender.start();
//
//        AppenderRef ref= AppenderRef.createAppenderRef("CONSOLE_APPENDER",null,null);
//        AppenderRef ref2 = AppenderRef.createAppenderRef("FILE_APPENDER", null, null);
//        AppenderRef[] refs = new AppenderRef[] {ref, ref2};
//        LoggerConfig loggerConfig= LoggerConfig.createLogger("false", Level.INFO,"CONSOLE_LOGGER","com",refs,null,config,null);
//        loggerConfig.addAppender(consoleAppender,null,null);
//        loggerConfig.addAppender(fileAppender, null, null);
//
//        config.addAppender(consoleAppender);
//        config.addLogger("com", loggerConfig);
//        ctx.updateLoggers(config);
//
//        return LogManager.getContext().getLogger("com");
    }

    public static void log(MBaseLog baseLog) {
        logger.info(baseLog.toString());
//        LogstashUtils.sendInfoToLogstash(LogstashIp, LogstashPort, MBaseLog.convertLog2JsonObejct(baseLog).toString());
    }

    public static void main(String[] args) {
        MFunctionCalledLog testLog = new MFunctionCalledLog();
        testLog.setLogDateTime(DateTime.now());
        testLog.setLogType(MLogType.FUNCTION_CALL);
        testLog.setLogObjectId("123-321-123-231");
        testLog.setLogMethodName("test");
        testLog.setLogUserId("user-123-321-123-321");
        testLog.setLogFromIpAddr("127.0.0.1");
        testLog.setLogFromPort(2222);
        testLog.setLogIpAddr("127.0.0.1");

        String str = MLogUtils.convertLogObjectToString(testLog);
        System.out.println(str);
        MLogUtils.log(testLog);

    }
}
