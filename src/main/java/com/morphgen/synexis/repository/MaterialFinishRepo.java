package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.MaterialFinish;

@Repository

public interface MaterialFinishRepo extends JpaRepository<MaterialFinish, Long> {
    
}
