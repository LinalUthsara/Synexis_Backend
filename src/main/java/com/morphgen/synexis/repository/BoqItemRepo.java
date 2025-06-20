package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.BoqItem;

@Repository

public interface BoqItemRepo extends JpaRepository<BoqItem, Long> {
    
}
