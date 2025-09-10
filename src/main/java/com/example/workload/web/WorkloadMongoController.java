package com.example.workload.web;


import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.model.TrainerWorkloadDocument;
import com.example.workload.mongo.repo.TrainerWorkloadRepository;
import com.example.workload.mongo.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;


@RestController
@RequestMapping("/api/v1/mongo/workloads")
@RequiredArgsConstructor
@Tag(name = "Workloads Mongo", description = "Mongo-backed trainer workloads")
public class WorkloadMongoController {


    private final TrainerWorkloadService service;
    private final TrainerWorkloadRepository repository;


    @PostMapping("/events")
    public ResponseEntity<Void> accept(@RequestBody @Valid WorkloadEventRequest request) {
        service.processEvent(request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadDocument> get(@PathVariable String username) {
        Optional<TrainerWorkloadDocument> doc = repository.findByTrainerUsername(username);
        return doc.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}