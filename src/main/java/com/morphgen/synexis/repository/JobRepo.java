package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.CostEstimation;
import com.morphgen.synexis.entity.Job;
import com.morphgen.synexis.enums.JobStatus;

@Repository

public interface JobRepo extends JpaRepository<Job, Long> {
    
    Optional<Job> findByEstimation(CostEstimation costEstimation);

    List<Job> findAllByOrderByJobIdDesc();

    List<Job> findByJobStatusOrderByJobIdDesc(JobStatus jobStatus);
    
}
