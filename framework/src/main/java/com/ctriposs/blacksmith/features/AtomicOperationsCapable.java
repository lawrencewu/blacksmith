package com.ctriposs.blacksmith.features;

/**
 * @author bulldog
 */
public interface AtomicOperationsCapable {
   boolean replace(String bucket, Object key, Object oldValue, Object newValue) throws Exception;
   Object putIfAbsent(String bucket, Object key, Object value) throws Exception;
   boolean remove(String bucket, Object key, Object oldValue) throws Exception;
}
