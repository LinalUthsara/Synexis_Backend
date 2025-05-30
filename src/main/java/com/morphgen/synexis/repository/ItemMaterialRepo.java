package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.ItemMaterial;

@Repository

public interface ItemMaterialRepo extends JpaRepository<ItemMaterial, Long> {
    
}
