package com.morphgen.synexis.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

public class CustomerDesignDto {
    
    private Long customerDesignId;

    private MultipartFile customerDesign;

}
