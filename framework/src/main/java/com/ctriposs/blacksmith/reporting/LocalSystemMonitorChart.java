package com.ctriposs.blacksmith.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctriposs.blacksmith.local.ReportDesc;
import com.ctriposs.blacksmith.local.ReportItem;
import com.ctriposs.blacksmith.sysmonitor.AbstractActivityMonitor;
import com.ctriposs.blacksmith.sysmonitor.CpuUsageMonitor;
import com.ctriposs.blacksmith.sysmonitor.GcMonitor;
import com.ctriposs.blacksmith.sysmonitor.LocalJmxMonitor;
import com.ctriposs.blacksmith.sysmonitor.MemoryUsageMonitor;
import com.ctriposs.blacksmith.sysmonitor.NetworkBytesMonitor;
import com.ctriposs.blacksmith.utils.Utils;

/**
 * @author bulldog
 */
public class LocalSystemMonitorChart {

   private static Logger log = LoggerFactory.getLogger(LocalSystemMonitorChart.class);
   private StringBuilder reportHeader;
   private ArrayList<String> reportStrings;
   private boolean hasNetworkStatistics = false;
   private TimeUnit chartTimeUnit = null;
   private int chartFrequency = 1;

   final Map<String, LocalJmxMonitor> sysMonitors;

   String reportPrefix;

   public LocalSystemMonitorChart(Map<String, LocalJmxMonitor> sysMonitors) {
      this.sysMonitors = new TreeMap<String, LocalJmxMonitor>(sysMonitors);
      LocalJmxMonitor ljm = this.sysMonitors.values().iterator().next();
      hasNetworkStatistics = ljm.getInterfaceName() != null;
      chartTimeUnit = ljm.getMeasuringUnit();
      chartFrequency = ljm.getMeasuringFrequency();
   }

   public void generate(ReportDesc reportDesc) {

      if (!reportDesc.isIncludeAll()) {
         if (reportDesc.getItems().isEmpty()) {
            log.info("No reports defined, not generating system monitor graphs.");
            return;
         }
         Map<String, LocalJmxMonitor> filter = new TreeMap<String, LocalJmxMonitor>();
         for (ReportItem reportItem : reportDesc.getItems()) {
            for (Map.Entry<String, LocalJmxMonitor> e : sysMonitors.entrySet()) {
               LocalJmxMonitor m = e.getValue();
               if (reportItem.matches(m.getProductName(), m.getConfigName())) {
                  filter.put(e.getKey(), e.getValue());
               }
            }
         }
         sysMonitors.clear();
         sysMonitors.putAll(filter);
         reportPrefix = reportDesc.getReportName();
      } else {
         reportPrefix = "All";
      }
      generateCpu();
      generateGc();
      generateMemory();
      if (hasNetworkStatistics) {
         generateNetwork();
      }
   }

   private void generateMemory() {
      reportHeader = new StringBuilder(chartTimeUnit.name());
      reportStrings = null;
      ClusterTimeSeriesReport timeReport = new ClusterTimeSeriesReport(chartFrequency, chartTimeUnit);
      timeReport.init("Time(" + chartTimeUnit.name() + ")", "Memory(Mb)", "Memory consumption", "");
      for (String s : sysMonitors.keySet()) {
         reportHeader.append(", mem-" + s);
         MemoryUsageMonitor memMonitor = sysMonitors.get(s).getMemoryMonitor();
         memMonitor.convertToMb();
         populateGraph(timeReport, "mem-" + s, memMonitor);
      }
      generateReport(timeReport, "memory_usage");
   }

   private void generateCpu() {
      reportHeader = new StringBuilder(chartTimeUnit.name());
      reportStrings = null;
      ClusterTimeSeriesReport timeReport = new ClusterTimeSeriesReport(chartFrequency, chartTimeUnit);
      timeReport.init("Time(" + chartTimeUnit.name() + ")", "CPU", "CPU Usage (%)", "");
      for (String s : sysMonitors.keySet()) {
         reportHeader.append(", cpu-" + s);
         CpuUsageMonitor cpuMonitor = sysMonitors.get(s).getCpuMonitor();
         populateGraph(timeReport, "cpu-" + s, cpuMonitor);
      }
      generateReport(timeReport, "cpu_usage");
   }

   private void generateGc() {
      reportHeader = new StringBuilder(chartTimeUnit.name());
      reportStrings = null;
      ClusterTimeSeriesReport timeReport = new ClusterTimeSeriesReport(chartFrequency, chartTimeUnit);
      timeReport.init("Time(" + chartTimeUnit.name() + ")", "GC", "GC Usage (%)", "");
      for (String s : sysMonitors.keySet()) {
         reportHeader.append(", gc-" + s);
         GcMonitor gcMonitor = sysMonitors.get(s).getGcMonitor();
         populateGraph(timeReport, "gc-" + s, gcMonitor);
      }
      generateReport(timeReport, "gc_usage");
   }

   private void generateNetwork() {
      reportHeader = new StringBuilder(chartTimeUnit.name());
      reportStrings = null;
      ClusterTimeSeriesReport timeReport = new ClusterTimeSeriesReport(chartFrequency, chartTimeUnit);
      timeReport.init("Time(" + chartTimeUnit.name() + ")", "Network(bytes)", "Network traffic", "");
      for (String s : sysMonitors.keySet()) {
         reportHeader.append(", network-inbound-" + s + ", network-outbound-" + s);
         NetworkBytesMonitor netInMonitor = sysMonitors.get(s).getNetworkBytesInMonitor();
         populateGraph(timeReport, "network-inbound-" + s, netInMonitor);
         NetworkBytesMonitor netOutMonitor = sysMonitors.get(s).getNetworkBytesOutMonitor();
         populateGraph(timeReport, "network-outbound-" + s, netOutMonitor);
      }
      generateReport(timeReport, "network_usage");
   }

   private void populateGraph(ClusterTimeSeriesReport timeReport, String s, AbstractActivityMonitor activityMonitor) {
      TimeSeries monitorData = timeReport.generateSeries(s, activityMonitor);
      int counter = 0;
      if (reportStrings == null) {
         reportStrings = new ArrayList<String>();
         for (Object item : monitorData.getItems()) {
            TimeSeriesDataItem tsdi = (TimeSeriesDataItem) item;
            reportStrings.add(counter++ + "," + tsdi.getValue());
         }
      } else {
         for (Object item : monitorData.getItems()) {
            /*
             * It's possible that one node gathered more data items than another node in the
             * cluster. If this happens, ignore those items.
             */
            if (counter < reportStrings.size()) {
               TimeSeriesDataItem tsdi = (TimeSeriesDataItem) item;
               reportStrings.set(counter, reportStrings.get(counter) + "," + tsdi.getValue());
            }
            counter++;
         }
      }
      timeReport.addSeries(monitorData);
   }

   private void generateReport(ClusterTimeSeriesReport timeReport, String fileNameNoExtension) {
      try {
         Utils.createOutputFile(reportPrefix + "-" + fileNameNoExtension + ".csv", generateReportCSV());
         TimeSeriesReportGenerator.generate(timeReport, "reports", reportPrefix + "-"
               + fileNameNoExtension);
      } catch (IOException e1) {
         log.error("Failed to write CSV file", e1);
      } catch (Exception e) {
         log.error(e.getMessage(), e);
      }
   }

   private String generateReportCSV() {
      StringBuilder reportCsvContent = new StringBuilder(reportHeader + "\n");
      for (String reportItem : reportStrings) {
         reportCsvContent.append(reportItem + "\n");
      }
      return reportCsvContent.toString();
   }

}
