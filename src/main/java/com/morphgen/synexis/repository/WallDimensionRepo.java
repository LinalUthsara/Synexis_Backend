package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.WallDimension;

@Repository

public interface WallDimensionRepo extends JpaRepository<WallDimension, Long> {
    
}
