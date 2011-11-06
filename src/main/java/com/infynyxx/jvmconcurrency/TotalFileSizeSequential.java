package com.infynyxx.jvmconcurrency;

import java.io.File;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class TotalFileSizeSequential {
    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile()) return file.length();
        
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null) {
            for (final File child : children) {
                total += getTotalSizeOfFilesInDir(child);
            }
        }
        return total;
    }
    
    public static void main(String[] args) {
        final long start = System.nanoTime();
        final long total = new TotalFileSizeSequential().getTotalSizeOfFilesInDir(new File("/etc"));
        final long end = System.nanoTime();
        System.out.println("Total Size: " + total);
        System.out.println("Total Time" + (end - start) / 1.0e9);
    }
}
