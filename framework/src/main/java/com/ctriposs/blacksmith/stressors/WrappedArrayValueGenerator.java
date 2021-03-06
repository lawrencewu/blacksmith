package com.ctriposs.blacksmith.stressors;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * @author bulldog
 */
public class WrappedArrayValueGenerator implements ValueGenerator {
   @Override
   public void init(String param, ClassLoader classLoader) {
   }

   @Override
   public Object generateValue(Object key, int size, Random r) {
      return new ByteArrayWrapper(ByteArrayValueGenerator.generateArray(size, r));
   }

   @Override
   public int sizeOf(Object value) {
      return ((ByteArrayWrapper) value).array.length;
   }

   @Override
   public boolean checkValue(Object value, int expectedSize) {
      if (!(value instanceof ByteArrayWrapper)) return false;
      ByteArrayWrapper wrapper = (ByteArrayWrapper) value;
      return wrapper.array != null && (expectedSize <= 0 || wrapper.array.length == expectedSize);
   }

   /* Because byte[].equals compares only pointers */
   public static class ByteArrayWrapper implements Serializable {
      private byte[] array;
      private transient int hashCode = 0;

      public ByteArrayWrapper(byte[] array) {
         this.array = array;
      }
      
      public byte[] getBytes() {
    	  return array;
      }

      @Override
      public int hashCode() {
         if (hashCode == 0) {
            hashCode = 42 + Arrays.hashCode(array);
            if (hashCode == 0) hashCode = 42;
         }
         return hashCode;
      }

      @Override
      public String toString() {
         return String.format("ByteArray[%d](%db)", hashCode(), array.length);
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof ByteArrayWrapper) {
            return Arrays.equals(array, ((ByteArrayWrapper) obj).array);
         }
         return false;
      }
   }
}
