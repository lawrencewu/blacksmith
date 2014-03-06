package com.ctriposs.blacksmith.features;

import com.ctriposs.blacksmith.stressors.KeyGenerator;

/**
 * Cache wrapper implementing this interface is supposed to produce its own key generator.
 *
 * @author bulldog
 */
public interface KeyGeneratorAware {

   KeyGenerator getKeyGenerator(int keyBufferSize);

}
