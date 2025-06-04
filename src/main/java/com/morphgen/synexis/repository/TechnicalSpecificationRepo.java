package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.TechnicalSpecification;

@Repository

public interface TechnicalSpecificationRepo extends JpaRepository<TechnicalSpecification, Long> {
    
}
