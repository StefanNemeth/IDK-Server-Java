/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.threadpools;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class WorkerTasks {
    private static Logger logger = Logger.getLogger(WorkerTasks.class);

    public static Executor getNettyWorkerExecutor() {
        return nettyWorkerExecutor;
    }

    public static Executor getNettyBossExecutor() {
        return nettyBossExecutor;
    }

    public static ScheduledThreadPoolExecutor getRoomExecutor() {
        return gameExecutor;
    }

    public static ScheduledThreadPoolExecutor getGameExecutor() {
        return gameExecutor;
    }

    public static ScheduledThreadPoolExecutor getSystemExecutor() {
        return systemExecutor;
    }

    public static int getServerType() {
        return serverType;
    }

    public static void initWorkerTasks(final int serverType) {
        WorkerTasks.serverType = serverType;
        WorkerTasks.nettyWorkerExecutor = Executors.newCachedThreadPool();
        WorkerTasks.nettyBossExecutor = Executors.newCachedThreadPool();
        WorkerTasks.gameExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
        WorkerTasks.systemExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        logger.info("Worker Tasks initialized.");
    }

    public static ScheduledFuture<?> addTask(final ServerTask task, final int initialDelay, final int period, final ScheduledThreadPoolExecutor worker) {
        return worker.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    // fields
    private static int serverType;
    private static Executor nettyWorkerExecutor;
    private static Executor nettyBossExecutor;
    private static ScheduledThreadPoolExecutor gameExecutor;
    private static ScheduledThreadPoolExecutor systemExecutor;
}
