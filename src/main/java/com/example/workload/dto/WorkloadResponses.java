package com.example.workload.dto;

import java.util.List;

public class WorkloadResponses {
    public record MonthlySummary(
            String trainerUsername,
            String trainerFirstName,
            String trainerLastName,
            boolean trainerStatus,
            int year,
            int month,
            int trainingSummaryDurationMinutes
    ) {}

    public record Month(int month, int trainingSummaryDurationMinutes) {}
    public record Year(int year, List<Month> months) {}

    public record FullSummary(
            String trainerUsername,
            String trainerFirstName,
            String trainerLastName,
            boolean trainerStatus,
            List<Year> years
    ) {}
}