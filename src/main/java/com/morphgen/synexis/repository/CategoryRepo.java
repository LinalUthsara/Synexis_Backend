package com.morphgen.synexis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Category;

@Repository

public interface CategoryRepo extends JpaRepository<Category, Long> {
    
    Optional<Category> findByCategoryName(String categoryName);

}
