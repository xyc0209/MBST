package com.mbs.info.collectors.LogCollector;

import com.mbs.common.log.MBaseLog;
import com.mbs.common.utils.LogstashUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LogFileTailerListener implements TailerListener {

    private Tailer tailer;
    private String logstashIp;
    private int logstashPort;

    private Logger logger = LogManager.getLogger(LogFileTailerListener.class);

    public LogFileTailerListener(String logstashIp, int logstashPort) {
        this.logstashPort = logstashPort;
        this.logstashIp = logstashIp;
    }

    public void init(Tailer tailer) {
        this.tailer = tailer;
    }

    public void fileNotFound() {
        logger.warn(tailer.getFile().getName() + " lost!");
        this.tailer.stop();
    }

    public void fileRotated() {

    }

    public void handle(String s) {
        logger.debug("Tailer handles: " + s);
        MBaseLog baseLog = MBaseLog.getLogFromStr(s);
        if (baseLog == null) {
            logger.debug("Failed to parse: " + s + ", ignored");
            return;
        }
        LogstashUtils.sendInfoToLogstash(logstashIp, logstashPort, MBaseLog.convertLog2JsonObejct(baseLog).toString());
    }

    public void handle(Exception e) {

    }
}
