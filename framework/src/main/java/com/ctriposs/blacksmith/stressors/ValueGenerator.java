package com.ctriposs.blacksmith.stressors;

import java.util.Random;

/**
 * Factory class which generates the values used for stress testing
 *
 * @author bulldog
 */
public interface ValueGenerator {

   String VALUE_GENERATOR = "VALUE_GENERATOR";

   /**
    * @param param   Generic argument for the generator
    * @param classLoader Class loader that should be used if the generator will load some classes via reflection.
    *
    */
   void init(String param, ClassLoader classLoader);

   Object generateValue(Object key, int size, Random random);

   int sizeOf(Object value);

   boolean checkValue(Object value, int expectedSize);
}
