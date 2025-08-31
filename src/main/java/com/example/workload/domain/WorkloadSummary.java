package com.example.workload.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workload_summary")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WorkloadSummary {
    @EmbeddedId
    private WorkloadKey id;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int totalMinutes; // aggregated per (username, year, month)
}