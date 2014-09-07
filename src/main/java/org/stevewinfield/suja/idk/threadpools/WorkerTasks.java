/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.threadpools;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class WorkerTasks {
    private static final Logger logger = Logger.getLogger(WorkerTasks.class);

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

    /**
     * Adds a repeating task
     * @param task Task to execute
     * @param initialDelay Delay before executing the first the task for the first time
     * @param period Delay between executions of this task
     * @param worker Worker that executes this task
     * @return a ScheduledFuture representing pending completion of the task, and whose get() method will throw an exception upon cancellation
     */
    public static ScheduledFuture<?> addTask(final ServerTask task, final int initialDelay, final int period, final ScheduledThreadPoolExecutor worker) {
        return worker.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Adds a repeating task with the default system executor
     * @param task Task to execute
     * @param initialDelay Delay before executing the first the task for the first time
     * @param period Delay between executions of this task
     * @return a ScheduledFuture representing pending completion of the task, and whose get() method will throw an exception upon cancellation
     */
    public static ScheduledFuture<?> addTask(final ServerTask task, final int initialDelay, final int period) {
        return addTask(task, initialDelay, period, systemExecutor);
    }

    /**
     * Adds a repeating task with the default system executor and the first execution after a delay of period
     * @param task Task to execute
     * @param period Delay between executions of this task
     * @return a ScheduledFuture representing pending completion of the task, and whose get() method will throw an exception upon cancellation
     */
    public static ScheduledFuture<?> addTask(final ServerTask task, final int period) {
        return addTask(task, period, period);
    }

    // fields
    private static int serverType;
    private static Executor nettyWorkerExecutor;
    private static Executor nettyBossExecutor;
    private static ScheduledThreadPoolExecutor gameExecutor;
    private static ScheduledThreadPoolExecutor systemExecutor;
}
