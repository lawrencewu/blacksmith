package com.ctriposs.blacksmith.stressors;

import com.ctriposs.blacksmith.CacheWrapperStressor;
import com.ctriposs.blacksmith.config.Property;
import com.ctriposs.blacksmith.config.Stressor;
import com.ctriposs.blacksmith.state.SlaveState;

/**
 * @author bulldog
 */
@Stressor(doc = "Ancestor of all common stressors")
public abstract class AbstractCacheWrapperStressor implements CacheWrapperStressor {

   @Property(doc = "Should be the JVM monitored during this stressor run? Default is false.")
   private boolean sysMonitorEnabled = false;

   //The object which will store the state of the stressors.
   protected SlaveState slaveState;

   @Override
   public boolean isSysMonitorEnabled() {
      return sysMonitorEnabled;
   }

   public void setSlaveState(SlaveState slaveState) {
      this.slaveState = slaveState;
   }
}
