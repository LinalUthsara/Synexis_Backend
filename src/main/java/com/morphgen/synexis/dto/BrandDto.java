package com.morphgen.synexis.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

public class BrandDto {
    
    private String brandName;

    private String brandDescription;

    private String brandCountry;

    private String brandWebsite;

    private MultipartFile brandImage;

}
