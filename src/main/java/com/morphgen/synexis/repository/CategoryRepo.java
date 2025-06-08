package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Category;

@Repository

public interface CategoryRepo extends JpaRepository<Category, Long> {
    
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findAllByOrderByCategoryIdDesc();

    List<Category> findByParentCategory_CategoryIdOrderByCategoryNameAsc(Long parentCategoryId);

    @Query("""
    SELECT c FROM Category c 
    WHERE c.categoryStatus = ACTIVE 
        AND c.parentCategory IS NULL 
        AND (
            LOWER(c.categoryName) LIKE LOWER(CONCAT(:searchCategory, '%')) 
            OR LOWER(c.categoryName) LIKE LOWER(CONCAT('% ', :searchCategory, '%')) 
            OR LOWER(c.categoryDescription) LIKE LOWER(CONCAT(:searchCategory, '%')) 
            OR LOWER(c.categoryDescription) LIKE LOWER(CONCAT('% ', :searchCategory, '%'))
        )
    """)
    List<Category> searchActiveParentCategories(@Param("searchCategory") String searchCategory);

    @Query("""
    SELECT c FROM Category c 
    WHERE c.categoryStatus = ACTIVE 
        AND c.parentCategory.categoryId = :parentCategoryId
        AND (
            LOWER(c.categoryName) LIKE LOWER(CONCAT(:searchCategory, '%')) 
            OR LOWER(c.categoryName) LIKE LOWER(CONCAT('% ', :searchCategory, '%')) 
            OR LOWER(c.categoryDescription) LIKE LOWER(CONCAT(:searchCategory, '%')) 
            OR LOWER(c.categoryDescription) LIKE LOWER(CONCAT('% ', :searchCategory, '%'))
        )
    """)
    List<Category> searchActiveSubCategoriesByParent(@Param("parentCategoryId") Long parentCategoryId,
        @Param("searchCategory") String searchCategory);

}
