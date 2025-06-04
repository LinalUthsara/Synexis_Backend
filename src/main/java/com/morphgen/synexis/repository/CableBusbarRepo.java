package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.CableBusbar;

@Repository

public interface CableBusbarRepo extends JpaRepository<CableBusbar, Long> {
    
}
