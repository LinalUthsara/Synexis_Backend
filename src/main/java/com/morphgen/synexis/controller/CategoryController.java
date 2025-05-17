package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.CategoryDropDownDto;
import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.dto.CategorySideDropViewDto;
import com.morphgen.synexis.dto.CategoryTableViewDto;
import com.morphgen.synexis.dto.CategoryViewDto;
import com.morphgen.synexis.service.CategoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/synexis/category")
@CrossOrigin("*")

public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryDto categoryDto) throws IOException {

        if(categoryDto.getCategoryName() == null || categoryDto.getCategoryName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Category Name is Required!");
        }

        categoryService.createCategory(categoryDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Category successfully created!");
    
    }

    @GetMapping
    public ResponseEntity<List<CategoryTableViewDto>> viewBrandTable() {
        
        List<CategoryTableViewDto> categoryTableViewDtoList = categoryService.viewCategoryTable();

        return ResponseEntity.status(HttpStatus.OK).body(categoryTableViewDtoList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryViewDto> viewCategoryById(@PathVariable Long categoryId){

        CategoryViewDto categoryViewDto = categoryService.viewCategoryById(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(categoryViewDto);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<CategorySideDropViewDto>> viewCategorySdieDrop(){

        List<CategorySideDropViewDto> categorySideDropViewDtoList = categoryService.viewCategorySideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(categorySideDropViewDtoList);

    }

    @GetMapping("/categoryDropDown")
    public ResponseEntity<List<CategoryDropDownDto>> categoryDropDown(){

        List<CategoryDropDownDto> categoryDropDownDtoList = categoryService.categoryDropDown();

        return ResponseEntity.status(HttpStatus.OK).body(categoryDropDownDtoList);
    }

    @GetMapping("/subDropDown/{parentCategoryId}")
    public ResponseEntity<List<CategoryDropDownDto>> subCategoryDropDown(@PathVariable Long parentCategoryId){

        List<CategoryDropDownDto> categoryDropDownDtoList = categoryService.subCategoryDropDown(parentCategoryId);

        return ResponseEntity.status(HttpStatus.OK).body(categoryDropDownDtoList);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto){
        
        categoryService.updateCategory(categoryId, categoryDto);

        return ResponseEntity.status(HttpStatus.OK).body("Category successfully updated!");
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
        
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body("Category successfully deleted!");
    }
    
}