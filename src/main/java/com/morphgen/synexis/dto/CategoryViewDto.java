package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class CategoryViewDto {
    
    private Long categoryId;

    private String categoryName;

    private String categoryDescription;

    private String parentCategoryName;

    private Long parentCategoryId;
    
    private Status categoryStatus;

    private List<MaterialTableViewDto> materialTableViewDtoList;

}
