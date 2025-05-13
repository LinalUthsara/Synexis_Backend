package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CategoryUpdateDto {
    
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String parentCategory;
    private Status categoryStatus;
}
