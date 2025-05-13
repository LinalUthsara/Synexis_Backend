package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class CategoryViewDto {
    
    private Long categoryId;
    private String categoryName;
    private String categotyDescription;
    private String parentCategoryName;
    private Status categotyStatus;

}
