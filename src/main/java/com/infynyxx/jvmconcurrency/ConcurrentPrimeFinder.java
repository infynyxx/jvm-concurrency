package com.infynyxx.jvmconcurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class ConcurrentPrimeFinder extends AbstractPrimeFinder {
    
    private final int poolSize;
    private final int theNumParts;
    
    public ConcurrentPrimeFinder(final int poolSize, final int theNumParts) {
        this.poolSize = poolSize;
        this.theNumParts = theNumParts;
    }

    @Override
    public int countPrimes(int number) {
        int count = 0;
        try {
            
            
            final List<Callable<Integer>> partitions = new ArrayList<Callable<Integer>>();
            final int chunksPerPartition = number / theNumParts;
            for (int i = 0; i < theNumParts; i++) {
                final int lower = (i * chunksPerPartition) + 1;
                final int upper = (i == theNumParts - 1) ? number : lower + chunksPerPartition + 1;
                partitions.add(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        return countPrimesInRange(lower, upper);
                    }
                });
            }
            
            final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
            final List<Future<Integer>> resultsFromParts = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
            executorPool.shutdown();
            for (final Future<Integer> resultFromPart : resultsFromParts) {
                try {
                    count += resultFromPart.get();
                } catch (ExecutionException ex) {
                    Logger.getLogger(ConcurrentPrimeFinder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(ConcurrentPrimeFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
}
