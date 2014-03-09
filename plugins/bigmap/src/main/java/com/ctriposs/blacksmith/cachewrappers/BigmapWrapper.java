package com.ctriposs.blacksmith.cachewrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.ctriposs.blacksmith.CacheWrapper;
import com.ctriposs.blacksmith.features.AtomicOperationsCapable;
import com.ctriposs.blacksmith.stressors.WrappedArrayValueGenerator.ByteArrayWrapper;
import com.ctriposs.blacksmith.utils.TypedProperties;
import com.ctriposs.bigmap.*;

public class BigmapWrapper implements CacheWrapper, AtomicOperationsCapable {

   public BigConcurrentHashMapImpl bigmap;

   @Override
   public void setUp(String configuration, boolean isLocal, int nodeIndex, TypedProperties confAttributes) throws Exception {
	   String mapDir = confAttributes.getProperty("map_dir", "/bigmap_test");
	   String mapName = confAttributes.getProperty("map_name", "bigmap");
	   bigmap = new BigConcurrentHashMapImpl(mapDir, mapName);
	   bigmap.removeAll();
   }

   public void tearDown() throws Exception {
	   bigmap.removeAll();
	   bigmap.close();
   }

   @Override
   public boolean isRunning() {
      return true;
   }
   
   private byte[] getBytes(Object o) {
	   if (o instanceof String) {
		   return ((String)o).getBytes();
	   } else if (o instanceof byte[]) {
		   return (byte[])o;
	   } else if (o instanceof ByteArrayWrapper) {
		   return ((ByteArrayWrapper)o).getBytes();
	   } else if (o instanceof Serializable) {
		   ByteArrayOutputStream bos = new ByteArrayOutputStream();
		   ObjectOutput out = null;
		   try {
		     out = new ObjectOutputStream(bos);   
		     out.writeObject(o);
		     byte[] bytes = bos.toByteArray();
		     return bytes;
		   } catch(Exception e) {
			   return null;
		   } finally {
		     try {
		       if (out != null) {
		         out.close();
		       }
		     } catch (IOException ex) {
		       // ignore close exception
		     }
		     try {
		       bos.close();
		     } catch (IOException ex) {
		       // ignore close exception
		     }
		   }
	   }
	   
	   throw new RuntimeException("Fail to convert object to bytes");
   }

   @Override
   public void put(String bucket, Object key, Object value) throws Exception {
      bigmap.put(getBytes(key), getBytes(value));
   }

   @Override
   public Object get(String bucket, Object key) throws Exception {
      return bigmap.get(getBytes(key));
   }
   
   @Override
   public Object remove(String bucket, Object key) throws Exception {
      return bigmap.remove(getBytes(key));
   }

   @Override
   public boolean replace(String bucket, Object key, Object oldValue, Object newValue) throws Exception {
      return bigmap.replace(getBytes(key), getBytes(oldValue), getBytes(newValue));
   }

   @Override
   public Object putIfAbsent(String bucket, Object key, Object value) throws Exception {
	   boolean isByteArrayWrapper = value instanceof ByteArrayWrapper;
      Object o = bigmap.putIfAbsent(getBytes(key), getBytes(value));
      if (o != null && isByteArrayWrapper) { 
    	  return new ByteArrayWrapper((byte[])o);// needed for StressTestStressor equals check
      } else {
    	  return o;
      }
   }

   @Override
   public boolean remove(String bucket, Object key, Object oldValue) throws Exception {
      return bigmap.remove(getBytes(key), getBytes(oldValue));
   }

   @Override
   public void clear(boolean local) throws Exception {
      bigmap.clear();
   }

   public int getNumMembers() {
      return 1;
   }

   public String getInfo() {
      return "Bigmap wrapper";
   }

   @Override
   public Object getReplicatedData(String bucket, String key) throws Exception {
      return null;
   }

   @Override
   public boolean isTransactional(String bucket) {
      return false;
   }

   public void startTransaction() {
      throw new IllegalStateException("This is not transactional");
   }

   public void endTransaction(boolean successful) {
   }

   @Override
   public int getLocalSize() {
	   return bigmap.size();
   }
   
   @Override
   public int getTotalSize() {
      return bigmap.size();
   }
}
