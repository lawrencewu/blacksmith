package com.ctriposs.blacksmith.stressors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctriposs.blacksmith.CacheWrapper;
import com.ctriposs.blacksmith.config.Property;
import com.ctriposs.blacksmith.config.Stressor;

@Stressor(doc = "Repeats the StressTestStressor logic with increasing amount of client threads.")
public class ClientStressTestStressor extends StressTestStressor {
   private static Logger log = LoggerFactory.getLogger(ClientStressTestStressor.class);

   @Property(doc = "Initial number of threads. Default is 1.")
   private int initThreads = 1;

   @Property(doc = "Maximum number of threads. Default is 10.")
   private int maxThreads = 10;

   @Property(doc = "Number of threads by which the actual number of threads will be incremented. Default is 1.")
   private int increment = 1;
   private double requestPerSec = 0;

   public Map<String, Object> stress(CacheWrapper wrapper) {
      init(wrapper);
      log.info("Client stress test with " + initThreads + " - " + maxThreads + " (increment " + increment + ")");
      
      int iterations = (maxThreads + increment - 1 - initThreads) / increment + 1;
      
           
      Map<String, Object> results = new LinkedHashMap<String, Object>();
      int iteration = 0;

      if (!startOperations()) return results;
      for (int threads = initThreads; threads <= maxThreads; threads += increment, iteration++) {
         log.info("Starting iteration " + iteration + " with " + threads);
         
         StressorCompletion completion;
         if (getDurationMillis() > 0) {
            completion = new TimeStressorCompletion(getDurationMillis() / iterations);
         } else {
            completion = new OperationCountCompletion(new AtomicInteger(getNumRequests() / iterations));
         }
         setStressorCompletion(completion);
         
         super.setNumThreads(threads);
         try {
            executeOperations();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
         if (isTerminated()) {
            break;
         }
         processResults(String.format("%03d", iteration), threads, results);
      }
      results.put(Statistics.REQ_PER_SEC, requestPerSec);
      
      finishOperations();      
      return results;
   }

   protected Map<String, Object> processResults(String iteration, int threads, Map<String, Object> results) {
      Statistics stats = createStatistics();
      for (Stressor stressor : stressors) {
         stats.merge(stressor.getStats());
      }
      results.putAll(stats.getResultsMap(threads, iteration + "."));
      requestPerSec = Math.max(requestPerSec, stats.getOperationsPerSecond(true));
      return results;
   }
         
   @Override
   @Deprecated
   public int getNumThreads() {
      throw new UnsupportedOperationException("Set initThreads, maxThreads and increment instead");
   }
   
   @Override
   @Deprecated
   public void setNumThreads(int numberOfThreads) {
      throw new UnsupportedOperationException("Set initThreads, maxThreads and increment instead");
   }
}
