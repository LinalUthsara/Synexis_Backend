package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.CustomerDesign;

@Repository

public interface CustomerDesignRepo extends JpaRepository<CustomerDesign, Long> {
    
}
