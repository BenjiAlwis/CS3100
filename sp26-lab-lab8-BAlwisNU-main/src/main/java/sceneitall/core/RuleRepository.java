package sceneitall.core;

import java.util.List;

import sceneitall.models.AutomationRule;

/** Persistence interface for Automation Rules. */
public interface RuleRepository {
  List<AutomationRule> loadRules(String homeId);
}
