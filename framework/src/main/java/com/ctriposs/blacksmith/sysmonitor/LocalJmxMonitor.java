package com.ctriposs.blacksmith.sysmonitor;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This program is designed to show how remote JMX calls can be used to retrieve metrics of remote
 * JVMs. To monitor other JVMs, make sure these are started with:
 * <p/>
 * -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false
 * -Dcom.sun.management.jmxremote.ssl=false
 * <p/>
 * And then simply subsitute the IP address in the JMX url by the IP address of the node.
 * 
 * @author bulldog
 */
public class LocalJmxMonitor implements Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = 2530981300271084693L;

   private String productName;
   private String configName;
   private String interfaceName;

   private static Logger log = LoggerFactory.getLogger(LocalJmxMonitor.class);
   private int measuringFrequency = 1;
   private TimeUnit measuringUnit = TimeUnit.SECONDS;

   private volatile CpuUsageMonitor cpuMonitor;
   private volatile MemoryUsageMonitor memoryMonitor;
   private volatile GcMonitor gcMonitor;
   private volatile NetworkBytesMonitor netInMonitor;
   private volatile NetworkBytesMonitor netOutMonitor;

   ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

   public void startMonitoringLocal() {

      log.info("Gathering statistics every " + measuringFrequency + " " + measuringUnit.name());
      try {
         cpuMonitor = new CpuUsageMonitor();
         exec.scheduleAtFixedRate(cpuMonitor, 0, measuringFrequency, measuringUnit);
         memoryMonitor = new MemoryUsageMonitor();
         exec.scheduleAtFixedRate(memoryMonitor, 0, measuringFrequency, measuringUnit);
         gcMonitor = new GcMonitor();
         exec.scheduleAtFixedRate(gcMonitor, 0, measuringFrequency, measuringUnit);
         if (interfaceName != null) {
            netInMonitor = NetworkBytesMonitor.createReceiveMonitor(interfaceName);
            exec.scheduleAtFixedRate(netInMonitor, 0, measuringFrequency, measuringUnit);
            netOutMonitor = NetworkBytesMonitor.createTransmitMonitor(interfaceName);
            exec.scheduleAtFixedRate(netOutMonitor, 0, measuringFrequency, measuringUnit);
         }
      } catch (Exception e) {
         log.error(e.getMessage(), e);
      }
   }
   
   public void stopMonitoringLocal() {
      cpuMonitor.stop();
      memoryMonitor.stop();
      gcMonitor.stop();
      if (interfaceName != null) {
         netInMonitor.stop();
         netOutMonitor.stop();
      }
      exec.shutdownNow();
      this.exec = null;
      StringBuffer result = new StringBuffer("Cpu measurements = " + cpuMonitor.getMeasurementCount() + ", memory measurements = "
            + memoryMonitor.getMeasurementCount() + ", gc measurements = " + gcMonitor.getMeasurementCount());
      if (interfaceName != null) {
         result.append(", network inbound measurements = " + netInMonitor.getMeasurementCount());
         result.append(", network outbound measurements = " + netOutMonitor.getMeasurementCount());
      }
      log.trace(result.toString());
   }

   public CpuUsageMonitor getCpuMonitor() {
      return cpuMonitor;
   }

   public MemoryUsageMonitor getMemoryMonitor() {
      return memoryMonitor;
   }

   public GcMonitor getGcMonitor() {
      return gcMonitor;
   }

   public NetworkBytesMonitor getNetworkBytesInMonitor() {
      return netInMonitor;
   }

   public NetworkBytesMonitor getNetworkBytesOutMonitor() {
      return netOutMonitor;
   }

   public String getProductName() {
      return productName;
   }

   public void setProductName(String productName) {
      this.productName = productName;
   }

   public String getConfigName() {
      return configName;
   }

   public void setConfigName(String configName) {
      this.configName = configName;
   }

   public String getInterfaceName() {
      return this.interfaceName;
   }

   public void setInterfaceName(String interfaceName) {
      this.interfaceName = interfaceName;
   }

   public TimeUnit getMeasuringUnit() {
      return measuringUnit;
   }

   public void setMeasuringUnit(TimeUnit measuringUnit) {
      this.measuringUnit = measuringUnit;
   }

   public int getMeasuringFrequency() {
      return measuringFrequency;
   }

   public void setMeasuringFrequency(int measuringFrequency) {
      this.measuringFrequency = measuringFrequency;
   }
}
