package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class CategoryTableViewDto {
    
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String mainCategoryName;
    private Status categoryStatus;

}
