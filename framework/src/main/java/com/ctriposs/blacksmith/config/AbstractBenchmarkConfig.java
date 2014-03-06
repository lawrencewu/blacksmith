package com.ctriposs.blacksmith.config;

/**
 * @author bulldog
 */
public abstract class AbstractBenchmarkConfig implements Cloneable {
   protected String productName;
   protected String configName;

   public abstract void validate();

   public abstract void errorOnCurrentBenchmark();

   public String getProductName() {
      return productName;
   }

   public String getConfigName() {
      return configName;
   }

   public void setProductName(String productName) {
      assertNo_(productName);
      this.productName = productName;
   }

   public void setConfigName(String configName) {
      assertNo_(configName);
      this.configName = configName;
   }

   private void assertNo_(String name) {
      if (name.indexOf("_") >= 0) {
         throw new RuntimeException("'_' not allowed in productName (reporting relies on that)");
      }
   }
}
