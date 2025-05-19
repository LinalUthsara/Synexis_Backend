package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.CostEstimation;

@Repository

public interface CostEstimationRepo extends JpaRepository<CostEstimation, Long> {
    
}
