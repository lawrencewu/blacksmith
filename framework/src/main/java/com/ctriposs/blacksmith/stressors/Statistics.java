package com.ctriposs.blacksmith.stressors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author bulldog
 */
public interface Statistics extends Serializable {
   long NS_IN_SEC = 1000 * 1000 * 1000;
   long NS_IN_MS = 1000 * 1000;
   String REQ_PER_SEC = "REQ_PER_SEC";

   void registerRequest(long responseTime, long txOverhead, Operation operation);

   void registerError(long responseTime, long txOverhead, Operation operation);

   void reset(long time);

   Statistics copy();

   void merge(Statistics otherStats);

   Map<String, Object> getResultsMap(int threads, String prefix);

   double getOperationsPerSecond(boolean includeOverhead);
}
