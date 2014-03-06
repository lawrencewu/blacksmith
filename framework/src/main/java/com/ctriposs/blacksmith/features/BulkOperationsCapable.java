package com.ctriposs.blacksmith.features;

import java.util.Map;
import java.util.Set;

/**
 * The cachewrapper suports bulk operations
 *
 * @author bulldog
 */
public interface BulkOperationsCapable {
   Map<Object, Object> getAll(String bucket, Set<Object> keys, boolean preferAsync) throws Exception;
   /**
    * Returning previous entries from the cache is optional - if the cache
    * is not capable of that it should return null.
    */
   Map<Object, Object> putAll(String bucket, Map<Object, Object> entries, boolean preferAsync) throws Exception;
   /**
    * Returning previous entries from the cache is optional - if the cache
    * is not capable of that it should return null.
    */
   Map<Object, Object> removeAll(String bucket, Set<Object> keys, boolean preferAsync) throws Exception;
}
