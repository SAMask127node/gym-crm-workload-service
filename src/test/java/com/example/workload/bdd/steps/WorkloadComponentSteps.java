package com.example.workload.bdd.steps;

import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.repo.TrainerWorkloadRepository;
import com.example.workload.mongo.service.TrainerWorkloadService;
import io.cucumber.java.en.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkloadComponentSteps {

    private final TrainerWorkloadRepository repo;
    private final TrainerWorkloadService service;

    @Given("a clean workload repository")
    public void cleanRepo() { repo.deleteAll(); }

    @Given("an existing monthly total of {int} minutes for username {string} in {int}-{int}")
    public void seed(int minutes, String username, int year, int month) {
        // Seed via service ADD to keep same logic
        var req = new WorkloadEventRequest(username, "Tom", "Smith", true,
                LocalDate.of(year, month, 1), minutes, WorkloadEventRequest.ActionType.ADD);
        service.processEvent(req);
    }

    @When("an {word} event arrives for username {string} with {int} minutes on {string}")
    public void sendEvent(String action, String username, int minutes, String date) {
        var type = action.equalsIgnoreCase("ADD")
                ? WorkloadEventRequest.ActionType.ADD : WorkloadEventRequest.ActionType.DELETE;
        var req = new WorkloadEventRequest(username, "Tom", "Smith", true,
                LocalDate.parse(date), minutes, type);
        service.processEvent(req);
    }

    @Then("the monthly total for {string} in {int}-{int} should be {int}")
    public void assertTotal(String username, int year, int month, int expected) {
        var doc = repo.findByTrainerUsername(username).orElseThrow();
        var total = doc.getYears().stream().filter(y -> y.getYear() == year).findFirst().orElseThrow()
                .getMonths().stream().filter(m -> m.getMonth() == month).findFirst().orElseThrow()
                .getTrainingSummaryDuration();
        assertThat(total).isEqualTo(expected);
    }
}