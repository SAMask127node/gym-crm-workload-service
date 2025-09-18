package com.example.workload.bdd.hooks;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration
public class TestContainersConfig {

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry r) {
        // Mongo
        r.add("spring.data.mongodb.uri", () ->
                "mongodb://" + System.getProperty("MONGO_HOST","localhost") + ":"
                        + System.getProperty("MONGO_PORT","27017") + "/workloads");

        // Artemis
        r.add("spring.artemis.broker-url", () ->
                "tcp://" + System.getProperty("ARTEMIS_HOST","localhost") + ":"
                        + System.getProperty("ARTEMIS_PORT","61616"));
        r.add("spring.artemis.user", () -> "admin");
        r.add("spring.artemis.password", () -> "admin");
    }
}