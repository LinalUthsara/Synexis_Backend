package com.morphgen.synexis.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.dto.CategoryTableViewDto;
import com.morphgen.synexis.entity.Category;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.exception.CategoryNotFoundException;
import com.morphgen.synexis.repository.CategoryRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.CategoryService;

@Service

public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public Category createCategory(CategoryDto categoryDto) {

        Optional<Category> existingCategory = categoryRepo.findByCategoryName(categoryDto.getCategoryName());
        if(existingCategory.isPresent()){
            throw new DataIntegrityViolationException("A Category with the name " + categoryDto.getCategoryName() + " already exists!");
        }

        Category category = new Category();

        category.setCategoryName(categoryDto.getCategoryName());
        category.setCategoryDescription(categoryDto.getCategoryDescription());
        
        if (categoryDto.getParentCategoryId() != null) {
            Category parentCategory = categoryRepo.findById(categoryDto.getParentCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryDto.getParentCategoryId() + " is not found!"));
            category.setParentCategory(parentCategory);
        }

        Category newCategory = categoryRepo.save(category);

        activityLogService.logActivity(
            "Category", 
            newCategory.getCategoryId(), 
            newCategory.getCategoryName(), 
            Action.CREATE, 
            "Created Category: " + newCategory.getCategoryName());

        return newCategory;
    }

    @Override
    public List<CategoryTableViewDto> viewCategoryTable() {
        
        List<Category> categories = categoryRepo.findAllByOrderByCategoryIdDesc();

        List<CategoryTableViewDto> categoryTableViewDtoList = categories.stream().map(category ->{

            CategoryTableViewDto categoryTableViewDto = new CategoryTableViewDto();

            if (category.getParentCategory() != null) {
                
                categoryTableViewDto.setCategoryName(category.getCategoryName());
                categoryTableViewDto.setMainCategoryName(category.getParentCategory().getCategoryName());
            }else{

                categoryTableViewDto.setMainCategoryName(category.getCategoryName());
            }

            categoryTableViewDto.setCategoryId(category.getCategoryId());
            categoryTableViewDto.setCategoryDescription(category.getCategoryDescription());
            categoryTableViewDto.setCategoryStatus(category.getCategoryStatus());

            return categoryTableViewDto;
        }).collect(Collectors.toList());

        return categoryTableViewDtoList;
    }

}
