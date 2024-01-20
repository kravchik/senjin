package yk.senjin.util;

import java.util.function.Function;

import static yk.jcommon.utils.Threads.sleep;

/**
 * Created by yuri at 2023.03.15
 */
public class ThreadUtils {
    
    public static void ticker(long sleepMs, Runnable onInit, Function<Float, Boolean> ticker) {
        new Thread(() -> {
            try {
                if (onInit != null) onInit.run();
                long lastTick = System.currentTimeMillis();
                while (true) {
                    long curTime = System.currentTimeMillis();
                    if (!ticker.apply((curTime - lastTick) / 1000f)) break;
                    lastTick = curTime;
                    sleep(sleepMs);
                }
            } catch (Throwable t) {
                System.err.println("Error occurred");
                t.printStackTrace();
            }
        }).start();
    }
    
    public static void tickerNotThread(long sleepMs, Runnable onInit, Function<Float, Boolean> ticker) {
        try {
            if (onInit != null) onInit.run();
            long lastTick = System.currentTimeMillis();
            while (true) {
                long curTime = System.currentTimeMillis();
                if (!ticker.apply((curTime - lastTick) / 1000f)) break;
                lastTick = curTime;
                sleep(sleepMs);
            }
        } catch (Throwable t) {
            System.err.println("Error occurred");
            t.printStackTrace();
        }
    }

}
