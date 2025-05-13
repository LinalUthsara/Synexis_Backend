package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.dto.CategoryTableViewDto;
import com.morphgen.synexis.dto.CategoryViewDto;
import com.morphgen.synexis.entity.Category;

@Service

public interface CategoryService {
    
    Category createCategory(CategoryDto categoryDto);

    List<CategoryTableViewDto> viewCategoryTable();

    CategoryViewDto viewCategoryById(Long categoryId);

}

    // ResponseEntity<byte[]> viewBrandImage(Long brandId);
    // List<BrandTableViewDto> viewBrandTable();
    // List<BrandSideDropViewDto> viewBrandSideDrop();
    // BrandViewDto viewBrandById(Long brandId);