package com.infynyxx.jvmconcurrency;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class TotalFileSizeConcurrentNaive {
    private long getTotalSizeOfFilesInDir(final ExecutorService service, final File file) throws InterruptedException, ExecutionException, TimeoutException {
        if (file.isFile()) return file.length();
        
        long total = 0;
        final File[] children = file.listFiles();
        
        if (children != null) {
            final List<Future<Long>> partitialFutures = new ArrayList<Future<Long>>();
            for (final File child : children) {
                partitialFutures.add(service.submit(new Callable<Long>() {

                    public Long call() throws Exception {
                        return getTotalSizeOfFilesInDir(service, child);
                    }
                    
                }));
            }
            
            for (Future<Long> partial : partitialFutures) {
                total += partial.get(100, TimeUnit.SECONDS);
            }
        }
        
        return total;
    }
    
    private long getTotalSizeOfFile(final String filename) throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService service = Executors.newFixedThreadPool(100);
        try {
            return getTotalSizeOfFilesInDir(service, new File(filename));
        } finally {
            service.shutdown();
        }
    }
    
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        final long start = System.nanoTime();
        final long total = new TotalFileSizeConcurrentNaive().getTotalSizeOfFile("/etc");
        final long end = System.nanoTime();
        System.out.println("Total Size: " + total);
        System.out.println("Total Time" + (end - start) / 1.0e9);
    }
}
