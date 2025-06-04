package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.FloorDimension;

@Repository

public interface FloorDimensionRepo extends JpaRepository<FloorDimension, Long> {
    
}
