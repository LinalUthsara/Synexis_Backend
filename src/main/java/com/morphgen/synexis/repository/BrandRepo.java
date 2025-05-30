package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Brand;

@Repository

public interface BrandRepo extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrandName(String brandName);
    Optional<Brand> findByBrandWebsite(String brandWebsite);
    
    List<Brand> findAllByOrderByBrandIdDesc();
    List<Brand> findAllByOrderByBrandNameAsc();

    @Query("""
    SELECT b FROM Brand b 
    WHERE b.brandStatus = ACTIVE AND (
        LOWER(b.brandName) LIKE LOWER(CONCAT(:searchBrand, '%')) 
        OR LOWER(b.brandName) LIKE LOWER(CONCAT('% ', :searchBrand, '%'))
        OR LOWER(b.brandWebsite) LIKE LOWER(CONCAT(:searchBrand, '%'))
        OR LOWER(b.brandWebsite) LIKE LOWER(CONCAT('% ', :searchBrand, '%'))
        OR LOWER(b.brandDescription) LIKE LOWER(CONCAT(:searchBrand, '%'))
        OR LOWER(b.brandDescription) LIKE LOWER(CONCAT('% ', :searchBrand, '%'))
    )""")
    List<Brand> searchActiveBrands(@Param("searchBrand") String searchBrand);
    
}
