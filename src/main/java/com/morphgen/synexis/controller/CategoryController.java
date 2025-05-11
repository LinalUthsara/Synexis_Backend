package com.morphgen.synexis.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.CategoryDto;
import com.morphgen.synexis.service.CategoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
}