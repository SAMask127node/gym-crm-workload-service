package com.example.workload.service;

import com.example.workload.domain.WorkloadKey;
import com.example.workload.domain.WorkloadSummary;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.dto.WorkloadResponses;
import com.example.workload.repo.WorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class WorkloadService {
    private final WorkloadRepository repo;

    @Transactional
    public void applyEvent(WorkloadEventRequest req) {
        LocalDate d = req.trainingDate();
        WorkloadKey key = new WorkloadKey(req.trainerUsername(), d.getYear(), d.getMonthValue());
        WorkloadSummary row = repo.findById(key).orElseGet(() -> WorkloadSummary.builder()
                .id(key)
                .firstName(req.trainerFirstName())
                .lastName(req.trainerLastName())
                .active(req.isActive())
                .totalMinutes(0)
                .build());

        int delta = req.trainingDurationMinutes() * (req.actionType() == WorkloadEventRequest.ActionType.ADD ? 1 : -1);
        int newTotal = Math.max(0, row.getTotalMinutes() + delta); // do not go below zero

        row.setFirstName(req.trainerFirstName());
        row.setLastName(req.trainerLastName());
        row.setActive(Boolean.TRUE.equals(req.isActive()));
        row.setTotalMinutes(newTotal);
        repo.save(row);
        log.info("Workload updated: username={}, y={}, m={}, totalMinutes={}", key.getUsername(), key.getYear(), key.getMonth(), newTotal);
    }

    @Transactional(readOnly = true)
    public WorkloadResponses.MonthlySummary getMonthly(String username, int year, int month) {
        var key = new WorkloadKey(username, year, month);
        var row = repo.findById(key).orElse(null);
        if (row == null) {
            return new WorkloadResponses.MonthlySummary(username, null, null, false, year, month, 0);
        }
        return new WorkloadResponses.MonthlySummary(username, row.getFirstName(), row.getLastName(), row.isActive(), year, month, row.getTotalMinutes());
    }

    @Transactional(readOnly = true)
    public WorkloadResponses.FullSummary getFull(String username) {
        var list = repo.findByUsernameOrdered(username);
        String first = list.isEmpty() ? null : list.get(0).getFirstName();
        String last = list.isEmpty() ? null : list.get(0).getLastName();
        boolean active = list.isEmpty() ? false : list.get(0).isActive();

        Map<Integer, List<WorkloadSummary>> byYear = list.stream().collect(Collectors.groupingBy(w -> w.getId().getYear(), TreeMap::new, Collectors.toList()));
        var years = byYear.entrySet().stream()
                .map(e -> new WorkloadResponses.Year(e.getKey(), e.getValue().stream()
                        .sorted(Comparator.comparingInt(w -> w.getId().getMonth()))
                        .map(w -> new WorkloadResponses.Month(w.getId().getMonth(), w.getTotalMinutes()))
                        .toList()))
                .toList();

        return new WorkloadResponses.FullSummary(username, first, last, active, years);
    }
}