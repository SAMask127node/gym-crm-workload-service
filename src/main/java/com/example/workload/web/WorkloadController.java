package com.example.workload.web;

import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.dto.WorkloadResponses;
import com.example.workload.service.WorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workloads")
@RequiredArgsConstructor
@Tag(name = "Workloads", description = "Trainer monthly workload aggregation")
@Log4j2
public class WorkloadController {

    private final WorkloadService service;

    @Operation(summary = "Accept training event (ADD/DELETE)")
    @PostMapping("/events")
    public ResponseEntity<Void> accept(@RequestBody @Valid WorkloadEventRequest request) {
        service.applyEvent(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get monthly workload for trainer")
    @GetMapping("/{username}/summary")
    public ResponseEntity<WorkloadResponses.MonthlySummary> monthly(@PathVariable String username,
                                                                    @RequestParam int year,
                                                                    @RequestParam int month) {
        return ResponseEntity.ok(service.getMonthly(username, year, month));
    }

    @Operation(summary = "Get full workload for trainer (years -> months)")
    @GetMapping("/{username}")
    public ResponseEntity<WorkloadResponses.FullSummary> full(@PathVariable String username) {
        return ResponseEntity.ok(service.getFull(username));
    }
}