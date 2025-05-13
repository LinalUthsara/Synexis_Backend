package com.morphgen.synexis.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.dto.CategorySideDropViewDto;
import com.morphgen.synexis.dto.CategoryTableViewDto;
import com.morphgen.synexis.dto.CategoryUpdateDto;
import com.morphgen.synexis.dto.CategoryViewDto;
import com.morphgen.synexis.entity.Category;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.CategoryNotFoundException;
import com.morphgen.synexis.repository.CategoryRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.CategoryService;
import com.morphgen.synexis.utils.EntityDiffUtil;

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

    @Override
    public CategoryViewDto viewCategoryById(Long categoryId) {
        
        Category category = categoryRepo.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryId + " is not found!"));

        CategoryViewDto categoryViewDto = new CategoryViewDto();

        categoryViewDto.setCategoryId(categoryId);
        categoryViewDto.setCategoryName(category.getCategoryName());
        categoryViewDto.setCategotyDescription(category.getCategoryDescription());
        categoryViewDto.setCategotyStatus(category.getCategoryStatus());

        if (category.getParentCategory() != null) {
            categoryViewDto.setParentCategoryName(category.getParentCategory().getCategoryName());
        }

        return categoryViewDto;
    }

    @Override
    public List<CategorySideDropViewDto> viewCategorySideDrop() {
        
        List<Category> categories = categoryRepo.findAllByOrderByCategoryIdDesc();

        List<CategorySideDropViewDto> categorySideDropViewDtoList = categories.stream().map(category ->{

            CategorySideDropViewDto categorySideDropViewDto = new CategorySideDropViewDto();

            if (category.getParentCategory() != null) {
                
                categorySideDropViewDto.setCategoryName(category.getCategoryName());
                categorySideDropViewDto.setMainCategoryName(category.getParentCategory().getCategoryName());
            }else{

                categorySideDropViewDto.setMainCategoryName(category.getCategoryName());
            }

            categorySideDropViewDto.setCategoryId(category.getCategoryId());

            return categorySideDropViewDto;
        }).collect(Collectors.toList());

        return categorySideDropViewDtoList;
    }

    @Override
    public Category updateCategory(Long categoryId, CategoryDto categoryDto) {
        
        Category category = categoryRepo.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryId + " is not found!"));

        if (!category.getCategoryName().equalsIgnoreCase(categoryDto.getCategoryName())) {
         Optional<Category> oldCategory = categoryRepo.findByCategoryName(categoryDto.getCategoryName());
            if (oldCategory.isPresent()) {
                throw new DataIntegrityViolationException("A Category with the name " + categoryDto.getCategoryName() + " already exists!");
            }
        }

        CategoryUpdateDto existingCategory = CategoryUpdateDto.builder()
        .categoryId(category.getCategoryId())
        .categoryName(category.getCategoryName())
        .categoryDescription(category.getCategoryDescription())
        .categoryStatus(category.getCategoryStatus())
        .parentCategory(category.getParentCategory() != null ? category.getParentCategory().getCategoryName() : null)
        .build();

        category.setCategoryName(categoryDto.getCategoryName());
        category.setCategoryDescription(categoryDto.getCategoryDescription());

        if (categoryDto.getParentCategoryId() != null) {
            Category parentCategory = categoryRepo.findById(categoryDto.getParentCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryDto.getParentCategoryId() + " is not found!"));
            category.setParentCategory(parentCategory);
        }
        else{
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepo.save(category);

        CategoryUpdateDto newCategory = CategoryUpdateDto.builder()
        .categoryId(updatedCategory.getCategoryId())
        .categoryName(updatedCategory.getCategoryName())
        .categoryDescription(updatedCategory.getCategoryDescription())
        .categoryStatus(updatedCategory.getCategoryStatus())
        .parentCategory(updatedCategory.getParentCategory() != null ? updatedCategory.getParentCategory().getCategoryName() : null)
        .build();

        String changes = EntityDiffUtil.describeChanges(existingCategory, newCategory);

        activityLogService.logActivity(
            "Category", 
            updatedCategory.getCategoryId(), 
            updatedCategory.getCategoryName(), 
            Action.UPDATE, 
            changes.isBlank() ? "No changes detected" : changes);

            return updatedCategory;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        
        Category category = categoryRepo.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryId + " is not found!"));

        category.setCategoryStatus(Status.INACTIVE);

        categoryRepo.save(category);

            activityLogService.logActivity(
            "Category", 
            category.getCategoryId(), 
            category.getCategoryName(), 
            Action.DELETE, 
            "Deleted Category: " + category.getCategoryName());
    }

}