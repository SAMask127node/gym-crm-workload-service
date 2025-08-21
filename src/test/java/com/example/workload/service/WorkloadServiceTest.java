package com.example.workload.service;

import com.example.workload.domain.WorkloadKey;
import com.example.workload.domain.WorkloadSummary;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.repo.WorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkloadServiceTest {
    private WorkloadRepository repo;
    private WorkloadService service;

    @BeforeEach
    void setUp() {
        repo = mock(WorkloadRepository.class);
        service = new WorkloadService(repo);
    }

    @Test
    void addEvent_increasesMinutes() {
        var req = new WorkloadEventRequest("u1", "John", "Doe", true, LocalDate.of(2025, 8, 1), 60, WorkloadEventRequest.ActionType.ADD);
        when(repo.findById(any())).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service.applyEvent(req);
        verify(repo).save(argThat(w -> w.getTotalMinutes() == 60));
    }

    @Test
    void deleteEvent_neverBelowZero() {
        var key = new WorkloadKey("u1", 2025, 8);
        var existing = WorkloadSummary.builder().id(key).firstName("J").lastName("D").active(true).totalMinutes(30).build();
        when(repo.findById(any())).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var req = new WorkloadEventRequest("u1", "John", "Doe", true, LocalDate.of(2025, 8, 1), 60, WorkloadEventRequest.ActionType.DELETE);
        service.applyEvent(req);
        verify(repo).save(argThat(w -> w.getTotalMinutes() == 0));
    }
}