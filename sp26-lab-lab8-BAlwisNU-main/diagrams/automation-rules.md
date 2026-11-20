graph LR
  subgraph Domain["Automation Rules Domain"]
    ARE[AutomationRuleEngine]
    RR[RuleRepository <<interface>>]
    HUR[HomeUserRepository <<interface>>]
    NPR[NotificationPreferenceRepository <<interface>>]
    DSP[DeviceStatePort <<interface>>]
    NP[NotificationPort <<interface>>]
    QHP[QuietHoursPolicy]
    TP[TimeProvider <<interface>>]
  end

  subgraph Production["Production Adapters"]
    DBRules[DatabaseRuleRepository]
    DBUsers[DatabaseHomeUserRepository]
    DBPrefs[DatabaseNotificationPreferenceRepository]
    Zigbee[ZigbeeDeviceStateAdapter]
    HttpNotif[HttpNotificationAdapter]
    SysTime[SystemTimeProvider]
  end

  subgraph Tests["Test Doubles"]
    MemRules[InMemoryRuleRepository]
    MemUsers[InMemoryHomeUserRepository]
    MemPrefs[InMemoryNotificationPreferenceRepository]
    FakeState[FakeDeviceStatePort]
    SpyNotif[SpyNotificationPort]
    FixedTime[FixedTimeProvider]
  end

  ARE --> RR
  ARE --> HUR
  ARE --> NPR
  ARE --> DSP
  ARE --> NP
  ARE --> QHP
  QHP --> TP

  DBRules --> RR
  DBUsers --> HUR
  DBPrefs --> NPR
  Zigbee --> DSP
  HttpNotif --> NP
  SysTime --> TP

  MemRules --> RR
  MemUsers --> HUR
  MemPrefs --> NPR
  FakeState --> DSP
  SpyNotif --> NP
  FixedTime --> TP