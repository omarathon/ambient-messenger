package dev.omarathon.ambientmessenger.garbagecollector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class RepeatingTask {
    private TimeUnit cooldownTimeUnit;
    private long cooldownAmount;
    private static ScheduledExecutorService scheduledExecutorService = null;

    public RepeatingTask(long cooldownAmount, TimeUnit cooldownTimeUnit) {
        this.cooldownAmount = cooldownAmount;
        this.cooldownTimeUnit = cooldownTimeUnit;
    }

    public void begin() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                RepeatingTask.this.run();
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 0, cooldownAmount, cooldownTimeUnit);
        scheduledExecutorService.shutdown();
    }

    public void stop() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }

    public abstract void run();
}
