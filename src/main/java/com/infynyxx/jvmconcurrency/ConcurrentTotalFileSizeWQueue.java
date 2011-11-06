package com.infynyxx.jvmconcurrency;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class ConcurrentTotalFileSizeWQueue {
    private ExecutorService service;
    private final BlockingQueue<Long> fileSizes = new ArrayBlockingQueue<Long>(500);
    private final AtomicLong pendingFileVisits = new AtomicLong();
    
    private void startExploreDir(final File file) {
        pendingFileVisits.incrementAndGet();
        service.execute(new Runnable() {

            public void run() {
                exploreDir(file);
            }
        });
    }
    
    private void exploreDir(final File file) {
        long fileSize = 0;
        if (file.isFile()) {
            fileSize = file.length();
        } else {
            final File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isFile()) {
                        fileSize += child.length();
                    } else {
                        startExploreDir(child);
                    }
                }
            }
        }
        
        try {
            fileSizes.put(fileSize);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
        pendingFileVisits.decrementAndGet();
    }
    
    private long getTotalSizeOfFile(File file) throws InterruptedException {
        service = Executors.newFixedThreadPool(100);
        try {
            startExploreDir(file);
            long totalSize = 0;
            while (pendingFileVisits.get() > 0 || fileSizes.size() > 0) {
                final long size = fileSizes.poll(10, TimeUnit.SECONDS);
                totalSize += size;
            }
            return totalSize;
        } finally {
            service.shutdown();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        final long start = System.nanoTime();
        final long total = new ConcurrentTotalFileSizeWQueue().getTotalSizeOfFile(new File("/usr"));
        final long end = System.nanoTime();
        System.out.println("Total Size: " + total);
        System.out.println("Total Time" + (end - start) / 1.0e9);
    }
}
