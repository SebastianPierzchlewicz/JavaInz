package pl.inz.praca.WsbPracaInz.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadExecutor {

    public static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(4);
    public static ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);


}
