package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class BrandTableViewDto {

    private Long brandId;
    
    private String brandName;

    private String brandDescription;

    private String brandCountry;

    private String brandImageUrl;

    private Status brandStatus;

}
