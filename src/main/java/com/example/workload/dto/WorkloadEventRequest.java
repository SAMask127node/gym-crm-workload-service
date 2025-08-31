package com.example.workload.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record WorkloadEventRequest(
        @NotBlank String trainerUsername,
        @NotBlank String trainerFirstName,
        @NotBlank String trainerLastName,
        @NotNull Boolean isActive,
        @NotNull @PastOrPresent LocalDate trainingDate,
        @Positive int trainingDurationMinutes,
        @NotNull ActionType actionType
) {
    public enum ActionType { ADD, DELETE }
}