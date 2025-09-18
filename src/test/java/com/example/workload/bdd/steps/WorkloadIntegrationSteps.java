package com.example.workload.bdd.steps;

import com.example.workload.mongo.repo.TrainerWorkloadRepository;
import com.example.workload.messaging.TrainingEventMessage; // from your JMS schema
import io.cucumber.java.en.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import java.time.Duration;

import static com.example.workload.messaging.JmsConfig.TRAINING_EVENTS_QUEUE;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkloadIntegrationSteps {

    private static final MongoDBContainer mongo =
            new MongoDBContainer(DockerImageName.parse("mongo:7"));
    private static final GenericContainer<?> artemis =
            new GenericContainer<>(DockerImageName.parse("vromero/activemq-artemis:2.31.2"))
                    .withEnv("ARTEMIS_USERNAME","admin")
                    .withEnv("ARTEMIS_PASSWORD","admin")
                    .withExposedPorts(61616, 8161);

    static {
        mongo.start();
        artemis.start();
        // You must configure your Spring context to pick these dynamic URLs, e.g. using
        // spring.test.context.DynamicPropertySource in a @TestConfiguration (see note below).
    }

    private final TrainerWorkloadRepository repo;
    private final JmsTemplate jmsTemplate;

    @LocalServerPort int port; // if you run webEnvironment=RANDOM_PORT

    @Given("ActiveMQ broker is running") public void brokerUp() {
        assertThat(artemis.isRunning()).isTrue();
    }

    @Given("MongoDB is running") public void mongoUp() {
        assertThat(mongo.isRunning()).isTrue();
    }

    @Given("workload-service is connected to broker and Mongo") public void svcReady() {
        // Sanity check: repo call works (Mongo connected)
        repo.deleteAll();
    }

    @Given("Mongo already has {int} minutes for {string} in {int}-{int}")
    public void seed(int minutes, String username, int year, int month) {
        // publish an ADD to seed, reusing the real listener path:
        publish(username, minutes, LocalDate.of(year, month, 1), TrainingEventMessage.ActionType.ADD);
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertTotal(username, year, month, minutes)
        );
    }

    @When("main-service publishes {word} event for {string} of {int} minutes on {string}")
    public void publishEvent(String action, String username, int minutes, String isoDate) {
        var type = action.equalsIgnoreCase("ADD")
                ? TrainingEventMessage.ActionType.ADD
                : TrainingEventMessage.ActionType.DELETE;
        publish(username, minutes, LocalDate.parse(isoDate), type);
    }

    @Then("Mongo must contain total {int} minutes for {string} in {int}-{int}")
    public void assertMongo(int expected, String username, int year, int month) {
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertTotal(username, year, month, expected)
        );
    }

    // helpers
    private void publish(String username, int minutes, LocalDate date, TrainingEventMessage.ActionType type) {
        var msg = new TrainingEventMessage(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                username, "Tom", "Smith", true, date, minutes, type, Instant.now()
        );
        jmsTemplate.convertAndSend(TRAINING_EVENTS_QUEUE, msg);
    }

    private void assertTotal(String username, int year, int month, int expected) {
        var doc = repo.findByTrainerUsername(username).orElseThrow();
        var y = doc.getYears().stream().filter(v -> v.getYear() == year).findFirst().orElseThrow();
        var m = y.getMonths().stream().filter(v -> v.getMonth() == month).findFirst().orElseThrow();
        assertThat(m.getTrainingSummaryDuration()).isEqualTo(expected);
    }
}