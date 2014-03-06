package com.ctriposs.blacksmith.utils;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Type-aware properties.  Extends the JDK {@link Properties} class to provide accessors that convert values to certain
 * types, using default values if a conversion is not possible.
 *
 * @author bulldog
 */
public class TypedProperties extends Properties {

   /** The serialVersionUID */
   private static final long serialVersionUID = 3799321248100686287L;

   private static final Logger log = LoggerFactory.getLogger(TypedProperties.class);

   /**
    * Copy constructor
    *
    * @param p properties instance to from.  If null, then it is treated as an empty Properties instance.
    */
   public TypedProperties(Properties p) {
      if (p != null && !p.isEmpty()) putAll(p);
   }

   /**
    * Default constructor that returns an empty instance
    */
   public TypedProperties() {

   }

   /**
    * Factory method that converts a JDK {@link Properties} instance to an instance of TypedProperties, if needed.
    *
    * @param p properties to convert.
    * @return A TypedProperties object.  Returns an empty TypedProperties instance if p is null.
    */
   public static TypedProperties toTypedProperties(Properties p) {
      if (p instanceof TypedProperties) return (TypedProperties) p;
      return new TypedProperties(p);
   }

   public int getIntProperty(String key, int defaultValue) {
      String value = getProperty(key);
      if (value == null) return defaultValue;
      value = value.trim();
      if (value.length() == 0) return defaultValue;


      try {
         return Integer.parseInt(value);
      }
      catch (NumberFormatException nfe) {
         log.warn("Unable to convert string property [" + value + "] to an int!  Using default value of " + defaultValue);
         return defaultValue;
      }
   }

   public long getLongProperty(String key, long defaultValue, boolean doStringReplace) {
      String value = getProperty(key);
      if (value == null) return defaultValue;
      value = value.trim();
      if (value.length() == 0) return defaultValue;


      try {
         return Long.parseLong(value);
      }
      catch (NumberFormatException nfe) {
         log.warn("Unable to convert string property [" + value + "] to a long!  Using default value of " + defaultValue);
         return defaultValue;
      }
   }

   public boolean getBooleanProperty(String key, boolean defaultValue) {
      String value = getProperty(key);
      if (value == null) return defaultValue;
      value = value.trim();
      if (value.length() == 0) return defaultValue;

      try {
         return Boolean.parseBoolean(value);
      }
      catch (Exception e) {
         log.warn("Unable to convert string property [" + value + "] to a boolean!  Using default value of " + defaultValue);
         return defaultValue;
      }
   }
}