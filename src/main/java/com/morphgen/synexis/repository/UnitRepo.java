package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Unit;

@Repository

public interface UnitRepo extends JpaRepository<Unit, Long> {
    
    Optional<Unit> findByUnitName(String unitName);
    Optional<Unit> findByUnitShortName(String unitShortName);
    List<Unit> findAllByOrderByUnitIdDesc();
    List<Unit> findByBaseUnitIsNullOrderByUnitNameAsc();
    List<Unit> findByBaseUnit_UnitIdOrderByUnitNameAsc(Long baseUnitId);

    @Query("""
    SELECT u FROM Unit u 
    WHERE u.unitStatus = ACTIVE 
        AND u.baseUnit IS NULL 
        AND (
            LOWER(u.unitName) LIKE LOWER(CONCAT(:searchUnit, '%')) 
            OR LOWER(u.unitName) LIKE LOWER(CONCAT('% ', :searchUnit, '%')) 
            OR LOWER(u.unitShortName) LIKE LOWER(CONCAT(:searchUnit, '%')) 
            OR LOWER(u.unitShortName) LIKE LOWER(CONCAT('% ', :searchUnit, '%'))
        )
    """)
    List<Unit> searchActiveBaseUnits(@Param("searchUnit") String searchUnit);

    @Query("""
    SELECT u FROM Unit u 
    WHERE u.unitStatus = ACTIVE 
        AND u.baseUnit.unitId = :baseUnitId 
        AND (
            LOWER(u.unitName) LIKE LOWER(CONCAT(:searchUnit, '%')) 
            OR LOWER(u.unitName) LIKE LOWER(CONCAT('% ', :searchUnit, '%')) 
            OR LOWER(u.unitShortName) LIKE LOWER(CONCAT(:searchUnit, '%')) 
            OR LOWER(u.unitShortName) LIKE LOWER(CONCAT('% ', :searchUnit, '%'))
        )
    """)
    List<Unit> searchActiveSubUnitsByBase(@Param("baseUnitId") Long baseUnitId, @Param("searchUnit") String searchUnit);

}