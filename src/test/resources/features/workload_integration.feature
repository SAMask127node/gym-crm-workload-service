Feature: Integration via ActiveMQ
  Background:
    Given ActiveMQ broker is running
    And MongoDB is running
    And workload-service is connected to broker and Mongo

  Scenario: End-to-end message updates Mongo (positive)
    When main-service publishes ADD event for "t.smith" of 45 minutes on "2025-09-05"
    Then Mongo must contain total 45 minutes for "t.smith" in 2025-09

  Scenario: Large delete clamps to zero (negative)
    Given Mongo already has 30 minutes for "t.smith" in 2025-09
    When main-service publishes DELETE event for "t.smith" of 100 minutes on "2025-09-06"
    Then Mongo must contain total 0 minutes for "t.smith" in 2025-09