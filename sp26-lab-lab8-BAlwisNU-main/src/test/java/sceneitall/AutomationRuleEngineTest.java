package sceneitall;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import sceneitall.core.AutomationRuleEngine;

/** Tests for AutomationRuleEngine. */
class AutomationRuleEngineTest {

  @Test
  @Disabled("Requires Zigbee network")
  void testRuleFires_whenMotionDetected() {
    AutomationRuleEngine engine = new AutomationRuleEngine();
    engine.evaluate("home-123");
  }

  @Test
  @Disabled("Requires database connection")
  void testLoadsRulesFromDatabase() {
    AutomationRuleEngine engine = new AutomationRuleEngine();
    engine.evaluate("home-1");
  }
}
