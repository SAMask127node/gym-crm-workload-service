package com.example.workload.mongo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.*;


@Document("trainer_workloads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@CompoundIndexes({
        @CompoundIndex(name = "idx_first_last_name", def = "{ 'trainerFirstName': 1, 'trainerLastName': 1 }")
})
public class TrainerWorkloadDocument {
    @Id
    private String id; // Mongo _id


    @Indexed(unique = true)
    private String trainerUsername;


    private String trainerFirstName;
    private String trainerLastName;
    private boolean trainerStatus; // boolean type


    @Builder.Default
    private List<YearNode> years = new ArrayList<>();


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class YearNode {
        private int year;
        @Builder.Default
        private List<MonthNode> months = new ArrayList<>();
    }


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MonthNode {
        private int month; // 1..12
        private int trainingSummaryDuration; // number type
    }
}