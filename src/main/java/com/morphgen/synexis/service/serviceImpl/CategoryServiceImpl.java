package com.morphgen.synexis.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.CategoryDropDownDto;
import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.dto.CategorySideDropViewDto;
import com.morphgen.synexis.dto.CategoryTableViewDto;
import com.morphgen.synexis.dto.CategoryUpdateDto;
import com.morphgen.synexis.dto.CategoryViewDto;
import com.morphgen.synexis.dto.MaterialTableViewDto;
import com.morphgen.synexis.dto.ParentCategoryDropDownDto;
import com.morphgen.synexis.entity.Category;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.NotificationType;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.CategoryNotFoundException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.repository.CategoryRepo;
import com.morphgen.synexis.repository.MaterialRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.CategoryService;
import com.morphgen.synexis.service.NotificationService;
import com.morphgen.synexis.utils.EntityDiffUtil;
import com.morphgen.synexis.utils.ImageUrlUtil;

@Service

public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private MaterialRepo materialRepo;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public Category createCategory(CategoryDto categoryDto) {

        if(categoryDto.getCategoryName() == null || categoryDto.getCategoryName().isEmpty()){
            throw new InvalidInputException("Category name cannot be empty!");
        }

        Optional<Category> existingCategory = categoryRepo.findByCategoryName(categoryDto.getCategoryName());
        if(existingCategory.isPresent()){

            Category activeCategory = existingCategory.get();
            
            if (activeCategory.getCategoryStatus() == Status.ACTIVE){

                throw new DataIntegrityViolationException("A Category with the name " + categoryDto.getCategoryName() + " already exists!");
            }
            else {

                throw new DataIntegrityViolationException("A Category with the name " + categoryDto.getCategoryName() + " already exists but is currently inactive. Consider reactivating it.");
            }
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

        notificationService.createNotification(
            "New Category Created", 
            category.getCategoryName() + " has been created in the inventory.", 
            NotificationType.INFO, 
            "CATEGORY");

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

        List<Material> materials = materialRepo.findMaterialsByCategoryId(categoryId);

        CategoryViewDto categoryViewDto = new CategoryViewDto();

        categoryViewDto.setCategoryId(categoryId);
        categoryViewDto.setCategoryName(category.getCategoryName());
        categoryViewDto.setCategoryDescription(category.getCategoryDescription());
        categoryViewDto.setCategoryStatus(category.getCategoryStatus());

        if (category.getParentCategory() != null) {
            categoryViewDto.setParentCategoryId(category.getParentCategory().getCategoryId());
            categoryViewDto.setParentCategoryName(category.getParentCategory().getCategoryName());
        }

        List<MaterialTableViewDto> materialTableViewDtoList = materials.stream().map(material ->{

            MaterialTableViewDto materialTableViewDto = new MaterialTableViewDto();

            materialTableViewDto.setMaterialId(material.getMaterialId());
            materialTableViewDto.setMaterialName(material.getMaterialName());
            materialTableViewDto.setMaterialDescription(material.getMaterialDescription());
            materialTableViewDto.setMaterialSKU(material.getMaterialSKU());
            materialTableViewDto.setMaterialPurchasePrice(material.getMaterialPurchasePrice());
            materialTableViewDto.setQuantityInHand(material.getQuantityInHand());
            materialTableViewDto.setMaterialStatus(material.getMaterialStatus());

            if (material.getMaterialImage() != null) {
                String imageUrl = ImageUrlUtil.constructMaterialImageUrl(material.getMaterialId());
                materialTableViewDto.setMaterialImageUrl(imageUrl);
            }

            return materialTableViewDto;
        }).collect(Collectors.toList());

        categoryViewDto.setMaterialTableViewDtoList(materialTableViewDtoList);

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
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDto categoryDto) {
        
        Category category = categoryRepo.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryId + " is not found!"));

        if(categoryDto.getCategoryName() == null || categoryDto.getCategoryName().isEmpty()){
            throw new InvalidInputException("Category name cannot be empty!");
        }

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

            notificationService.createNotification(
            "Category Updated", 
            category.getCategoryName() + " has been updated.", 
            NotificationType.WARNING, 
            "CATEGORY");

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

        notificationService.createNotification(
            "Category Deleted", 
            category.getCategoryName() + " has been deleted from the inventory.", 
            NotificationType.ALERT, 
            "CATEGORY");
    }

    @Override
    public List<ParentCategoryDropDownDto> parentCategoryDropDown(String searchParentCategory) {
        
        List<Category> categories = categoryRepo.searchActiveParentCategories(searchParentCategory.trim());

        List<ParentCategoryDropDownDto> categoryDropDownDtoList = categories.stream().map(category ->{

            ParentCategoryDropDownDto parentCategoryDropDownDto = new ParentCategoryDropDownDto();

            parentCategoryDropDownDto.setParentCategoryId(category.getCategoryId());
            parentCategoryDropDownDto.setParentCategoryName(category.getCategoryName());

            return parentCategoryDropDownDto;
        }).collect(Collectors.toList());

        return categoryDropDownDtoList;
    }

    @Override
    public List<CategoryDropDownDto> subCategoryDropDown(Long parentCategoryId, String searchSubCategory) {
        
        List<Category> categories = categoryRepo.searchActiveSubCategoriesByParent(parentCategoryId, searchSubCategory.trim());

        List<CategoryDropDownDto> categoryDropDownDtoList = categories.stream().map(category ->{

            CategoryDropDownDto categoryDropDownDto = new CategoryDropDownDto();

            categoryDropDownDto.setCategoryId(category.getCategoryId());
            categoryDropDownDto.setCategoryName(category.getCategoryName());

            return categoryDropDownDto;
        }).collect(Collectors.toList());

        return categoryDropDownDtoList;
    }

    @Override
    public void reactivateCategory(Long categoryId) {
        
        Category category = categoryRepo.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + categoryId + " is not found!"));

        if (category.getCategoryStatus() == Status.ACTIVE){
            throw new DataIntegrityViolationException("Category is already active!");
        }

        category.setCategoryStatus(Status.ACTIVE);

        categoryRepo.save(category);

            activityLogService.logActivity(
            "Category", 
            category.getCategoryId(), 
            category.getCategoryName(), 
            Action.REACTIVATE, 
            "Reactivated Category: " + category.getCategoryName());

        notificationService.createNotification(
            "Category Reactivated", 
            category.getCategoryName() + " has been reactivated.", 
            NotificationType.INFO, 
            "CATEGORY");

    }

}