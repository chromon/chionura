package com.chionura.utils;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * 服务请求超时处理。
 */
public class TimeoutUtils {

    private static final Logger log = Logger.getLogger(TimeoutUtils.class.getName());

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static String process(Callable<String> task, long timeout) {
        if (task == null) {
            return null;
        }

        Future<String> futureRet = executor.submit(task);

        try {
            return futureRet.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.severe("Interrupt Exception");
        } catch (ExecutionException e) {
            log.severe("Task execute exception");
        } catch (TimeoutException e) {
            log.warning("Process Timeout");
            if (futureRet != null && !futureRet.isCancelled()) {
                futureRet.cancel(true);
            }
            return "服务端处理请求超时！";
        }
        return null;
    }
}
