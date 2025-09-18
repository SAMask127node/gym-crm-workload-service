Feature: Workload aggregation
  As the system
  I want to aggregate monthly minutes
  So that trainer workloads are correct

  Background:
    Given a clean workload repository

  Scenario: Add minutes to a new trainer/month (positive)
    When an ADD event arrives for username "t.smith" with 90 minutes on "2025-09-01"
    Then the monthly total for "t.smith" in 2025-09 should be 90

  Scenario: Delete never drops below zero (negative)
    Given an existing monthly total of 30 minutes for username "t.smith" in 2025-09
    When a DELETE event arrives for username "t.smith" with 60 minutes on "2025-09-02"
    Then the monthly total for "t.smith" in 2025-09 should be 0