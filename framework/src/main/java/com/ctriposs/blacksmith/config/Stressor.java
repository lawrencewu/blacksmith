package com.ctriposs.blacksmith.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the class as being a stressor
 *
 * @author bulldog
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Stressor {
   static final String CLASS_NAME_WITHOUT_STRESSOR = "__class_name_without_Stressor__";
   static final String NO_DEPRECATED_NAME = "__no_deprecated_name__";
   String name() default CLASS_NAME_WITHOUT_STRESSOR;
   String deprecatedName() default NO_DEPRECATED_NAME;
   String doc();
}
