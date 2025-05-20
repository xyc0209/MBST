package com.badsmell.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class TimingThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(Runnable task) {
        long startTime = System.currentTimeMillis();
        super.execute(() -> {
            long queueTime = System.currentTimeMillis() - startTime;
            System.out.println("Queue time: " + queueTime + "ms");
            task.run();
        });
    }

    // 可以根据需要重写其他方法，如submit、submitList等

}