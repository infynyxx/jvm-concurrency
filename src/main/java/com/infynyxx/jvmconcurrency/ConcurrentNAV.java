package com.infynyxx.jvmconcurrency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class ConcurrentNAV extends AbstractNAV {

    @Override
    public double computeNetAssetValue(final Map<String, Integer> stocks) throws IOException, InterruptedException, ExecutionException {
        final int numerOfCores = Runtime.getRuntime().availableProcessors();
        final double blockingCofficient = 0.9;
        final int poolSize = (int)(numerOfCores / (1 - blockingCofficient));
        
        System.out.println("Number of Cores available is: " + numerOfCores);
        System.out.println("Pool Size is: " + poolSize);
        
        final List<Callable<Double>> partitions = new ArrayList<Callable<Double>>();
        for (final String ticker : stocks.keySet()) {
            partitions.add(new Callable<Double>() {

                public Double call() throws Exception {
                    return stocks.get(ticker) * YahooFinance.getPrice(ticker);
                }
            });
        }
        
        final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
        final List<Future<Double>> valueOfStocks = executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
        
        double netAssetValue = 0.0;
        for (final Future<Double> valueOfStock : valueOfStocks) {
            netAssetValue += valueOfStock.get();
        }
        
        executorPool.shutdown();
        return netAssetValue;
    }
    
}
