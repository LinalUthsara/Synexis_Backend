package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Material;

@Repository

public interface MaterialRepo extends JpaRepository<Material, Long> {
    
    Optional<Material> findByMaterialName(String materialName);
    Optional<Material> findByMaterialSKU(String materialSKU);
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

}