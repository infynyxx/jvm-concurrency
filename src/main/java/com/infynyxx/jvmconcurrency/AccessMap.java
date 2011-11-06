package com.infynyxx.jvmconcurrency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class AccessMap {
    private static void useMap(Map<String, Integer> scores) {
        scores.put("Fred", 10);
        scores.put("Shred", 10);
        
        try {
            for (final String key : scores.keySet()) {
                System.out.println(scores.get(key));
                scores.put("unix", 14);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        System.out.println("Number of elements in the map: " + scores.keySet().size());
    }
    
    
    public static void main(String[] args) {
        System.out.println("Using plain vanilla Map");
        //useMap(new HashMap<String, Integer>());
        
        System.out.println("Using synchronized Map");
        //useMap(Collections.synchronizedMap(new HashMap<String, Integer>()));
        
        System.out.println("Using concurrent HashMap");
        useMap(new ConcurrentHashMap<String, Integer>());
    }
}
