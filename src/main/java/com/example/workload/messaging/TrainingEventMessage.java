package com.example.workload.messaging;


import java.time.Instant;
import java.time.LocalDate;


public record TrainingEventMessage(
        String eventId, // for idempotency
        String trainingId, // optional but recommended
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean isActive,
        LocalDate trainingDate,
        int trainingDurationMinutes,
        ActionType actionType,
        Instant occurredAt
)
{
    public enum ActionType { ADD, DELETE }
}