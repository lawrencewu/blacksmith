package com.ctriposs.blacksmith.config;

/**
 * Abstracts the logic of parsing an configuration file.
 *
 * @author Mircea.Markus@jboss.com
 */
public abstract class ConfigParser {

   public static ConfigParser getConfigParser() {
      return new DomConfigParser();
   }
}
