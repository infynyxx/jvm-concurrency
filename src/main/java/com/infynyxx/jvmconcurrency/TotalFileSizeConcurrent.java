package com.infynyxx.jvmconcurrency;

import java.io.File;
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
public class TotalFileSizeConcurrent {
    class SubDirectoriesAndSize {
        final public long size;
        final public List<File> subDirectories;
        
        public SubDirectoriesAndSize(final long totalSize, final List<File> subDirectories) {
            size = totalSize;
            this.subDirectories = subDirectories;
        }
    }
    
    private SubDirectoriesAndSize getTotalAndSubDirs(final File file) {
        long total = 0;
        final List<File> subDirectories = new ArrayList<File>();
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isFile()) {
                        total += 0;
                    }
                    else {
                        subDirectories.add(child);
                    }
                }
            }
        }
        return new SubDirectoriesAndSize(total, subDirectories);
    }
    
    private long getTotalSizeOfFilesInDir(final File file) throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService service = Executors.newFixedThreadPool(100);
        try {
            long total = 0;
            final List<File> directories = new ArrayList<File>();
            directories.add(file);
            while (directories.isEmpty()) {
                final List<Future<SubDirectoriesAndSize>> partials = new ArrayList<Future<SubDirectoriesAndSize>>();
                for (final File directory : directories) {
                    partials.add(service.submit(new Callable<SubDirectoriesAndSize>() {

                        public SubDirectoriesAndSize call() throws Exception {
                            return getTotalAndSubDirs(directory);
                        }
                    }));
                }
                directories.clear();
                
                for (final Future<SubDirectoriesAndSize> partial : partials ) {
                    final SubDirectoriesAndSize subDirectoriesAndSize = partial.get(100, TimeUnit.SECONDS);
                    directories.addAll(subDirectoriesAndSize.subDirectories);
                    total += subDirectoriesAndSize.size;
                }
            }
            return total;
        } finally {
            service.shutdown();
        }
    }
    
    
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        final long start = System.nanoTime();
        final long total = new TotalFileSizeConcurrent().getTotalSizeOfFilesInDir(new File("/usr"));
        final long end = System.nanoTime();
        System.out.println("Total Size: " + total);
        System.out.println("Total Time" + (end - start) / 1.0e9);
    }
}
