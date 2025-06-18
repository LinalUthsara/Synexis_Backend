package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.entity.Unit;

@Repository

public interface MaterialRepo extends JpaRepository<Material, Long> {
    
    Optional<Material> findByMaterialName(String materialName);
    Optional<Material> findByMaterialSKU(String materialSKU);
    Optional<Material> findByMaterialPartNumber(String materialPartNumber);

    List<Material> findAllByOrderByMaterialIdDesc();

    @Query("SELECT m FROM Material m " +
       "WHERE m.baseUnit.unitId = :unitId OR m.otherUnit.unitId = :unitId")
    List<Material> findMaterialsByUnitId(@Param("unitId") Long unitId);

    @Query("SELECT m FROM Material m " +
       "WHERE m.category.categoryId = :categoryId OR m.subCategory.categoryId = :categoryId")
    List<Material> findMaterialsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT m FROM Material m " +
       "WHERE m.brand.brandId = :brandId")
    List<Material> findMaterialsByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(m) FROM Material m WHERE m.baseUnit = :unit OR m.otherUnit = :unit")
    Long countByUnitUsage(Unit unit);

    @Query("""
    SELECT m FROM Material m 
    WHERE m.materialStatus = ACTIVE AND (
        LOWER(m.materialName) LIKE LOWER(CONCAT(:searchMaterial, '%')) 
        OR LOWER(m.materialName) LIKE LOWER(CONCAT('% ', :searchMaterial, '%'))
        OR LOWER(m.materialSKU) LIKE LOWER(CONCAT(:searchMaterial, '%'))
        OR LOWER(m.materialSKU) LIKE LOWER(CONCAT('% ', :searchMaterial, '%'))
        OR LOWER(m.materialDescription) LIKE LOWER(CONCAT(:searchMaterial, '%'))
        OR LOWER(m.materialDescription) LIKE LOWER(CONCAT('% ', :searchMaterial, '%'))
        OR LOWER(m.materialPartNumber) LIKE LOWER(CONCAT(:searchMaterial, '%'))
        OR LOWER(m.materialPartNumber) LIKE LOWER(CONCAT('% ', :searchMaterial, '%'))
        OR LOWER(m.materialBarcode) LIKE LOWER(CONCAT(:searchMaterial, '%'))
        OR LOWER(m.materialBarcode) LIKE LOWER(CONCAT('% ', :searchMaterial, '%'))
        OR LOWER(m.materialBarcode) LIKE LOWER(CONCAT(:searchMaterial, '%'))
        OR LOWER(m.materialBarcode) LIKE LOWER(CONCAT('% ', :searchMaterial, '%'))
    )""")
    List<Material> searchActiveMaterials(@Param("searchMaterial") String searchMaterial);

}