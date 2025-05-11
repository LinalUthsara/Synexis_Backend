package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class CategoryDto {
    
    private String categoryName;
    private String categoryDescription;
    private Long parentCategoryId;

}
