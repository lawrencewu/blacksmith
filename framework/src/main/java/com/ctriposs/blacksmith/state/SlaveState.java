package com.ctriposs.blacksmith.state;

import com.ctriposs.blacksmith.CacheWrapper;

import java.net.InetAddress;

/**
 * State residing on slave, passed to each's {@link com.ctriposs.blacksmith.DistStage#initOnSlave(SlaveState)}
 *
 * @author bulldog
 */
public class SlaveState extends StateBase {

   private InetAddress masterAddress;
   private InetAddress localAddress;
   private CacheWrapper cacheWrapper;

   public InetAddress getMasterAddress() {
      return masterAddress;
   }

   public void setMasterAddress(InetAddress serverAddress) {
      this.masterAddress = serverAddress;
   }

   public InetAddress getLocalAddress() {
      return localAddress;
   }

   public void setLocalAddress(InetAddress localAddress) {
      this.localAddress = localAddress;
   }

   public void setCacheWrapper(CacheWrapper wrapper) {
      this.cacheWrapper = wrapper;
   }

   public CacheWrapper getCacheWrapper() {
      return cacheWrapper;
   }
}
