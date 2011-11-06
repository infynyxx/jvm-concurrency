package com.infynyxx.jvmconcurrency;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class SequentialPrimeFinder extends AbstractPrimeFinder {

    @Override
    public int countPrimes(int number) {
        return countPrimesInRange(1, number);
    }
    
}
