package com.morphgen.synexis.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

public class DesignDto {
    
    private Long designId;

    private MultipartFile design;
}
