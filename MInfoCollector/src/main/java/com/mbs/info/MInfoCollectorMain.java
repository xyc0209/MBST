package com.mbs.info;

import com.mbs.common.base.IInfoCollector;
import com.mbs.info.collectors.LogCollector.LogFileCollector;

import java.util.ArrayList;
import java.util.List;

public class MInfoCollectorMain {
    private List<IInfoCollector> collectorList;

    public MInfoCollectorMain() {
        this.collectorList = new ArrayList<>();
        this.collectorList.add(new LogFileCollector());
//        this.collectorList.add(new MetricsCollector());
    }

    public void start() {
        for (IInfoCollector iInfoCollector : this.collectorList) {
            iInfoCollector.start();
        }
    }

    public static void main(String[] args) {
        MInfoCollectorMain logCollectorMain = new MInfoCollectorMain();
        logCollectorMain.start();
    }
}
