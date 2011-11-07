package com.infynyxx.jvmconcurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class EnergySource {
    private final long MAXLEVEL = 100;
    private final AtomicLong level = new AtomicLong(MAXLEVEL);
    private static final ScheduledExecutorService replinishTimer = Executors.newScheduledThreadPool(10, new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            thread.setDaemon(true);
            return thread;
        }
    });
    private ScheduledFuture<?> replinishTask;
    
    private EnergySource() {}
    
    private void init() {
        replinishTask = replinishTimer.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                replinish();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    private void replinish() {
        if (level.get() < MAXLEVEL) {
            level.incrementAndGet();
        }
    }
    
    public static EnergySource create() {
        EnergySource energySource = new EnergySource();
        energySource.init();
        return energySource;
    }
    
    public long getUnitsAvailable() {
        return level.get();
    }
    
    public boolean useEnergy(final long units) {
        final long currentLevel = level.get();
        if (units > 0 && currentLevel >= units) {
            return level.compareAndSet(currentLevel, currentLevel - units);
        }
        return false;
    }
    
    public synchronized void stopEnergySource() {
        replinishTask.cancel(false);
    }
}
