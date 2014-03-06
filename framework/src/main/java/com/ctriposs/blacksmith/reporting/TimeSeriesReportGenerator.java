package com.ctriposs.blacksmith.reporting;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctriposs.blacksmith.utils.Utils;

/**
 * This class generates a TimeSeries chart appropriate for the AbstractActivityMonitor subclasses
 * 
 * @author bulldog
 */
public class TimeSeriesReportGenerator {

   private static Logger log = LoggerFactory.getLogger(TimeSeriesReportGenerator.class);

   public static void generate(ClusterTimeSeriesReport report, String reportDir, String fileName) throws IOException {
      File root = new File(reportDir);
      if (!root.exists()) {
         if (!root.mkdirs()) {
            log.warn("Could not create root dir : " + root.getAbsolutePath()
                  + " This might result in reports not being generated");
         } else {
            log.info("Created root file: " + root);
         }
      }
      File chartFile = new File(root, fileName + ".png");
      Utils.backupFile(chartFile);

      ChartUtilities.saveChartAsPNG(chartFile, createChart(report), 1024, 768);

      log.info("Chart saved as " + chartFile);
   }

   private static JFreeChart createChart(ClusterTimeSeriesReport report) {
      return ChartFactory.createTimeSeriesChart(report.getTitle(), report.getXLabel(), report.getYLabel(),
            report.getCategorySet(), true, false, false);
   }
}