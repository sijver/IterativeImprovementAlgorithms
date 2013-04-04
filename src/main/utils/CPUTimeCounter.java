package main.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Created with IntelliJ IDEA.
 */
public class CPUTimeCounter {

    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static long counterStartTime;

    public static void startCounter() {
        counterStartTime = threadMXBean.getCurrentThreadCpuTime();
    }

    public static long getCounterResult() {
        return threadMXBean.getCurrentThreadCpuTime() - counterStartTime;
    }

}
