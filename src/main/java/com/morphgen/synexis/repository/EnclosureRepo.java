package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Enclosure;

@Repository

public interface EnclosureRepo extends JpaRepository<Enclosure, Long> {
    
}
