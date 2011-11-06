package com.infynyxx.jvmconcurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class YahooFinance {
    public static double getPrice(final String ticket) throws MalformedURLException, IOException {
        final URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + ticket);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        
        //Date,Open,High,Low,Close,Volume,Adj Close 
        //2011-03-17,336.83,339.61,330.66,334.64,23519400,334.64
        final String discardHeader = reader.readLine();
        final String data = reader.readLine();
        final String[] dataItems = data.split(",");
        final double priceIsLastValue = Double.valueOf(dataItems[dataItems.length - 1]);
        
        return priceIsLastValue;
    }
}
