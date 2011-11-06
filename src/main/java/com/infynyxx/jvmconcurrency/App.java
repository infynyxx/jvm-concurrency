package com.infynyxx.jvmconcurrency;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException
    {
        //new ConcurrentNAV().timeAndComputeValue();
        System.out.println("*******");
        //new SequentialNAV().timeAndComputeValue();
        
        new SequentialPrimeFinder().timeAndCompute(10000000);
        System.out.println("*******");
        new ConcurrentPrimeFinder(4, 4).timeAndCompute(10000000);
        
    }
}
