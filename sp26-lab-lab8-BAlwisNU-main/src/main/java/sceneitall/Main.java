package sceneitall;

import sceneitall.core.AutomationRuleEngine;
import sceneitall.core.DatabaseDeviceRepository;
import sceneitall.core.DeviceManager;
import sceneitall.core.DeviceRepository;

public class Main {

  public static void main(String[] args) {
    DatabaseConnection db = DatabaseConnection.getInstance();

    // Composition root wiring (Device Management)
    DeviceRepository deviceRepo = new DatabaseDeviceRepository(db);
    DeviceManager deviceManager = new DeviceManager(deviceRepo);

    // Existing behavior (Automation Rules still legacy for now)
    var ruleEngine = new AutomationRuleEngine();
    ruleEngine.evaluate("home-1");

    // deviceManager is now DI-ready for future use
    if (deviceManager.getDevices("home-1").isEmpty()) {
      // no-op; just prevents "unused" warnings in some setups
    }
  }
}
