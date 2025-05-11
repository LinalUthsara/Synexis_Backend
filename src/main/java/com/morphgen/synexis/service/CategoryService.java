package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.entity.Category;

@Service

public interface CategoryService {
    
    Category createCategory(CategoryDto categoryDto);
}
