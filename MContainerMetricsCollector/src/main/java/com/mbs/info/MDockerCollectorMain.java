package com.mbs.info;

import com.mbs.info.collectors.MDockerMetricsCollector;


public class MDockerCollectorMain {
    public static void main(String[] args) {
        MDockerMetricsCollector dockerMetricsCollector = new MDockerMetricsCollector();
        dockerMetricsCollector.start();
    }
}
