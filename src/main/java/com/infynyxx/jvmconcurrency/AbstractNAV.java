package com.infynyxx.jvmconcurrency;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public abstract class AbstractNAV {
    public static Map<String, Integer> readTickers() throws FileNotFoundException, IOException {
        final BufferedReader reader = new BufferedReader(new FileReader("stocks.txt"));
        final Map<String, Integer> stocks = new HashMap<String, Integer>();
        String stockInfo = null;
        while ((stockInfo = reader.readLine()) != null) {
            final String[] stockInfoData = stockInfo.split(",");
            final String stockTicket = stockInfoData[0];
            final Integer quantity = Integer.valueOf(stockInfoData[1].trim());
            stocks.put(stockTicket, quantity);
        }
        return stocks;
    }
    
    public void timeAndComputeValue() throws IOException, InterruptedException, ExecutionException {
        final long start = System.nanoTime();
        final Map<String, Integer> stocks = readTickers();
        final double nav = computeNetAssetValue(stocks);
        final long end = System.nanoTime();
        final String value = new DecimalFormat("$##,##0.00").format(nav);
        System.out.println("Your net asset value is " + value);
        System.out.println("Time (seconds) taken " + (end - start) / 1.0e9);
    }
    
    public abstract double computeNetAssetValue(Map<String, Integer> stocks) throws IOException, InterruptedException, ExecutionException;
}
