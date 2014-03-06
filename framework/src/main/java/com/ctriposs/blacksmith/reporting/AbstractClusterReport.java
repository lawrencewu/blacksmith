package com.ctriposs.blacksmith.reporting;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store data for a cluster wide chart
 * 
 * @author bulldog
 */
public class AbstractClusterReport {

   private String xLabel;
   private String yLabel;
   private String title;
   private String subtitle;
   private List<String> notes = new ArrayList<String>();

   public AbstractClusterReport() {
      super();
   }

   public void init(String xLabel, String yLabel, String title, String subtitle) {
      this.xLabel = xLabel;
      this.yLabel = yLabel;
      this.title = title;
      this.subtitle = subtitle;
   }

   public void addNote(String note) {
      notes.add(note);
   }

   public String getTitle() {
      return title;
   }

   public String getSubtitle() {
      return subtitle;
   }

   public String getXLabel() {
      return xLabel;
   }

   public String getYLabel() {
      return yLabel;
   }

   public List<String> getNotes() {
      return notes;
   }

}