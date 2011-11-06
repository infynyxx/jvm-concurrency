package com.infynyxx.jvmconcurrency;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class SequentialNAV extends AbstractNAV {

    @Override
    public double computeNetAssetValue(final Map<String, Integer> stocks) throws IOException, InterruptedException, ExecutionException {
        double netAssetValue = 0.0;
        /**
        for (Entry<String, Integer> entry : stocks.entrySet()) {
            netAssetValue = entry.getValue() * YahooFinance.getPrice(entry.getKey());
        }
        **/
        for (String ticker : stocks.keySet()) {
            netAssetValue += stocks.get(ticker) * YahooFinance.getPrice(ticker);
        }
        return netAssetValue;
    }
    
}
