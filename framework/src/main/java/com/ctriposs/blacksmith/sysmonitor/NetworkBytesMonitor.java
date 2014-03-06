package com.ctriposs.blacksmith.sysmonitor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse the /proc/net/dev file for a value on the specified network interface
 * 
 * @author bulldog
 */
public class NetworkBytesMonitor extends AbstractActivityMonitor implements Serializable {
   
   private static int TRANSMIT_BYTES_INDEX = 8;
   private static int RECEIVE_BYTES_INDEX = 0;

   /** The serialVersionUID */
   private static final long serialVersionUID = -260611570251145013L;

   private static Logger log = LoggerFactory.getLogger(NetworkBytesMonitor.class);

   boolean running = true;
   String iface;
   int valueIndex = -1;
   BigDecimal initialValue;

   public static NetworkBytesMonitor createReceiveMonitor(String iface) {
      return new NetworkBytesMonitor(iface, RECEIVE_BYTES_INDEX);
   }

   public static NetworkBytesMonitor createTransmitMonitor(String iface) {
      return new NetworkBytesMonitor(iface, TRANSMIT_BYTES_INDEX);
   }

   private NetworkBytesMonitor(String iface, int valueIndex) {
      super();
      this.iface = iface;
      this.valueIndex = valueIndex;
   }

   public void stop() {
      running = false;
   }

   public void run() {
      if (running) {
         FileInputStream inputStream;
         try {
            inputStream = new FileInputStream("/proc/net/dev");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
               String line = br.readLine();
               while (line != null) {
                  line = line.trim();
                  if (line.startsWith(iface)) {
                     String[] vals = line.split(":")[1].trim().split("\\s+");
                     // Start monitoring from zero and then increase
                     if (initialValue == null) {
                        initialValue = new BigDecimal(vals[valueIndex]);
                        this.addMeasurement(new BigDecimal(0));
                     } else {
                        this.addMeasurement(new BigDecimal(vals[valueIndex]).subtract(initialValue));
                     }
                     break;
                  }
                  line = br.readLine();
               }
               br.close();
            } catch (Exception e) {
               log.error("Exception occurred while reading /proc/net/dev.", e);
            } finally {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (IOException e) {
                     log.error("Exception occurred while closing /proc/net/dev.", e);
                  }
               }
            }
         } catch (FileNotFoundException e) {
            log.error("File /proc/net/dev was not found!", e);
         }
      }
   }

}
