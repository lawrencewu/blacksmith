package com.ctriposs.blacksmith;

import java.util.Map;

/**
 * A stressor performs operations on a cache wrapper and returns the results of performing these operations as a Map.
 *
 * @author bulldog
 */
public interface CacheWrapperStressor {
   /**
    * Performs operations against the given wrapper and returns the results of these operations as a map. The map will be
    * further used for report generation.
    */
   Map<String, Object> stress(CacheWrapper wrapper);

   void destroy() throws Exception;

   boolean isSysMonitorEnabled();
}
