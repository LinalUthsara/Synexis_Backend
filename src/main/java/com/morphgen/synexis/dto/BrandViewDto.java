package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class BrandViewDto {
    
    private Long brandId;
    
    private String brandName;

    private String brandDescription;

    private String brandCountry;

    private String brandWebsite;

    private String brandImageUrl;

    private Status brandStatus;

    private List<MaterialTableViewDto> materialTableViewDtoList;

}
