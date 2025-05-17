package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Brand;

@Repository

public interface BrandRepo extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrandName(String brandName);
    List<Brand> findAllByOrderByBrandIdDesc();
    List<Brand> findAllByOrderByBrandNameAsc();
    
}
