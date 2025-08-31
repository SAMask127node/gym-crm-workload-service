package com.example.workload.repo;

import com.example.workload.domain.WorkloadKey;
import com.example.workload.domain.WorkloadSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkloadRepository extends JpaRepository<WorkloadSummary, WorkloadKey> {
    @Query("select w from WorkloadSummary w where w.id.username = :username order by w.id.year, w.id.month")
    List<WorkloadSummary> findByUsernameOrdered(@Param("username") String username);
}
