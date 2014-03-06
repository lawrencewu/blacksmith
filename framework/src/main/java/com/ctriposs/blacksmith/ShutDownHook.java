package com.ctriposs.blacksmith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * // TODO: Document this
 *
 * @author bulldog
 * @since 4.0
 */
public class ShutDownHook extends Thread {

   private static Logger log = LoggerFactory.getLogger(ShutDownHook.class);

   private static volatile boolean controlled = false;

   private String processDescription;

   public ShutDownHook(String processDescription) {
      this.processDescription = processDescription;
   }

   @Override
   public void run() {
      if (controlled) {
         log.info(processDescription + " is being shutdown");
      } else {
         log.warn(processDescription + ": unexpected shutdown!");
      }
   }

   public static void exit(int code) {
      controlled = true;
      System.exit(code);
   }
}
