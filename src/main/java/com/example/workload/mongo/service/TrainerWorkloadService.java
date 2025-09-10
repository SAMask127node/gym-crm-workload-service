package com.example.workload.mongo.service;


import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.model.TrainerWorkloadDocument;
import com.example.workload.mongo.model.TrainerWorkloadDocument.MonthNode;
import com.example.workload.mongo.model.TrainerWorkloadDocument.YearNode;
import com.example.workload.mongo.repo.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Log4j2
public class TrainerWorkloadService {


    private final TrainerWorkloadRepository repository;

    @Transactional
    public void processEvent(WorkloadEventRequest req) {
        LocalDate d = req.trainingDate();
        int year = d.getYear();
        int month = d.getMonthValue();
        int delta = req.trainingDurationMinutes() * (req.actionType() == WorkloadEventRequest.ActionType.DELETE ? -1 : 1);


        var doc = repository.findByTrainerUsername(req.trainerUsername())
                .orElseGet(() -> newDoc(req));


// keep names/status fresh from the event (source of truth)
        doc.setTrainerFirstName(req.trainerFirstName());
        doc.setTrainerLastName(req.trainerLastName());
        doc.setTrainerStatus(Boolean.TRUE.equals(req.isActive()));


        var yearNode = doc.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    var y = YearNode.builder().year(year).build();
                    doc.getYears().add(y);
                    return y;
                });


        var monthNode = yearNode.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    var m = MonthNode.builder().month(month).trainingSummaryDuration(0).build();
                    yearNode.getMonths().add(m);
                    return m;
                });


        int newValue = monthNode.getTrainingSummaryDuration() + delta;
        if (newValue < 0) newValue = 0; // safety clamp
        monthNode.setTrainingSummaryDuration(newValue);


        repository.save(doc);
        log.info("[workload-mongo] username={}, y={}, m={}, delta={}, newTotal={}",
                req.trainerUsername(), year, month, delta, newValue);
    }


    private TrainerWorkloadDocument newDoc(WorkloadEventRequest req) {
        var doc = TrainerWorkloadDocument.builder()
                .trainerUsername(req.trainerUsername())
                .trainerFirstName(req.trainerFirstName())
                .trainerLastName(req.trainerLastName())
                .trainerStatus(Boolean.TRUE.equals(req.isActive()))
                .build();
        return doc;
    }
}