package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.BoqItemMaterial;

@Repository

public interface BoqItemMaterialRepo extends JpaRepository<BoqItemMaterial, Long> {
    
}
