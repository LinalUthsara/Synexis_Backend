package com.morphgen.synexis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Unit;

@Repository

public interface UnitRepo extends JpaRepository<Unit, Long> {
    
    Optional<Unit> findByUnitName(String unitName);
    Optional<Unit> findByUnitShortName(String unitShortName);

}
