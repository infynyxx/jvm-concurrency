package com.infynyxx.jvmconcurrency;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class TotalFileSizeConcurrentWLatch {
    private ExecutorService service;
    final private AtomicLong pendingVisits = new AtomicLong();
    final private AtomicLong totalSize = new AtomicLong();
    final private CountDownLatch latch = new CountDownLatch(1);
    
    public TotalFileSizeConcurrentWLatch() {
        
    }
    
    private void updateTotalSizeOfFilesInDir(final File file) {
        long fileSize = 0;
        if (file.isFile()) {
            fileSize = file.length();
        } else {
            final File[] children = file.listFiles();
            if (children != null) {
                for (final File child : children) {
                    if (child.isFile()) {
                        fileSize += child.length();
                    } else {
                        pendingVisits.incrementAndGet();
                        service.execute(new Runnable() {
                            public void run() {
                                updateTotalSizeOfFilesInDir(child);
                            }
                        });
                    }
                }
            }
        }
        totalSize.addAndGet(fileSize);
        if (pendingVisits.decrementAndGet() == 0) {
            latch.countDown();
        }
    }
    
    private long getTotalSizeOfFile(final File file) throws InterruptedException {
        service = Executors.newFixedThreadPool(100);
        pendingVisits.incrementAndGet();
        try {
            updateTotalSizeOfFilesInDir(file);
            latch.await(100, TimeUnit.SECONDS);
            return totalSize.longValue();
        } finally {
            service.shutdown();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        final long start = System.nanoTime();
        final long total = new TotalFileSizeConcurrentWLatch().getTotalSizeOfFile(new File("/usr"));
        final long end = System.nanoTime();
        System.out.println("Total Size: " + total);
        System.out.println("Total Time" + (end - start) / 1.0e9);
    }
}
