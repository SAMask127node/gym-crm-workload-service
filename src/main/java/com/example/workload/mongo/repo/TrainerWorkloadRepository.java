package com.example.workload.mongo.repo;


import com.example.workload.mongo.model.TrainerWorkloadDocument;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;


public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkloadDocument, String> {
    Optional<TrainerWorkloadDocument> findByTrainerUsername(String trainerUsername);
}