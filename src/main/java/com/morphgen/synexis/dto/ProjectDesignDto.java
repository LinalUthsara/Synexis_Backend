package com.morphgen.synexis.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.morphgen.synexis.enums.DesignStatus;

import lombok.Data;

@Data

public class ProjectDesignDto {

    private DesignStatus designStatus;
    
    private List<MultipartFile> designs;

}
