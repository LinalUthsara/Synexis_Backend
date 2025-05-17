package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CategoryDropDownDto;
import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.dto.CategorySideDropViewDto;
import com.morphgen.synexis.dto.CategoryTableViewDto;
import com.morphgen.synexis.dto.CategoryViewDto;
import com.morphgen.synexis.entity.Category;

@Service

public interface CategoryService {
    
    Category createCategory(CategoryDto categoryDto);

    List<CategoryTableViewDto> viewCategoryTable();
    List<CategorySideDropViewDto> viewCategorySideDrop();
    CategoryViewDto viewCategoryById(Long categoryId);

    List<CategoryDropDownDto> categoryDropDown();
    List<CategoryDropDownDto> subCategoryDropDown(Long parentCategoryId);

    Category updateCategory(Long categoryId, CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

}
