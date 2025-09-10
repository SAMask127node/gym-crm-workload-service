package com.example.workload.mongo.service;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.model.TrainerWorkloadDocument;
import com.example.workload.mongo.repo.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class TrainerWorkloadServiceTest {


    TrainerWorkloadRepository repo;
    TrainerWorkloadService service;


    @BeforeEach
    void setup() {
        repo = mock(TrainerWorkloadRepository.class);
        service = new TrainerWorkloadService(repo);
    }


    @Test
    void creates_new_document_when_absent_and_sets_month_total() {
        var req = new WorkloadEventRequest("u1","John","Doe",true,
                LocalDate.of(2025,9,1), 90, WorkloadEventRequest.ActionType.ADD);
        when(repo.findByTrainerUsername("u1")).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));


        service.processEvent(req);


        verify(repo).save(argThat(doc ->
                doc.getTrainerUsername().equals("u1") &&
                        doc.getYears().size() == 1 &&
                        doc.getYears().get(0).getYear() == 2025 &&
                        doc.getYears().get(0).getMonths().get(0).getMonth() == 9 &&
                        doc.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration() == 90
        ));
    }


    @Test
    void increments_existing_month_total_and_never_below_zero() {
        var existing = TrainerWorkloadDocument.builder()
                .trainerUsername("u1")
                .trainerFirstName("J")
                .trainerLastName("D")
                .trainerStatus(true)
                .build();
        var year = TrainerWorkloadDocument.YearNode.builder().year(2025).build();
        var month = TrainerWorkloadDocument.MonthNode.builder().month(9).trainingSummaryDuration(30).build();
        year.getMonths().add(month);
        existing.getYears().add(year);


        when(repo.findByTrainerUsername("u1")).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));


// ADD +60 => 90
        var addReq = new WorkloadEventRequest("u1","John","Doe",true,
                LocalDate.of(2025,9,1), 60, WorkloadEventRequest.ActionType.ADD);
        service.processEvent(addReq);
        assertThat(month.getTrainingSummaryDuration()).isEqualTo(90);


// DELETE -200 => clamp to 0
        var delReq = new WorkloadEventRequest("u1","John","Doe",true,
                LocalDate.of(2025,9,2), 200, WorkloadEventRequest.ActionType.DELETE);
        service.processEvent(delReq);
        assertThat(month.getTrainingSummaryDuration()).isEqualTo(0);
    }
}