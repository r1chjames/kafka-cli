package com.r1chjames;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class AppRunner {

    final ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);

    void submit(final Runnable mode) {
        threadPool.submit(mode);
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted: " + e.getMessage());
        } finally {
            System.out.println("Thread pool has been shut down.");
            threadPool.shutdown();
        }
    }
}
