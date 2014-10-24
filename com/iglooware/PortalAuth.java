package com.iglooware;

public class PortalAuth {

/**
  * Notify administrator of users needing authentication
  */
public static void main(String[] args) {
    // create toaster manager
  Toaster toasterManager = new Toaster();
  toasterManager.setDisplayTime(4000);
  while(true) {
    try {
      toasterManager.showToaster(null, "Activate Users","http://192.168.200.55/admin/bristolinn/","Click to activate.");
      Thread.sleep(10000);
    }
    catch(Exception ex) {}
  }
}

}
