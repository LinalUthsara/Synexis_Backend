package com.morphgen.synexis.dto;

import java.util.List;

import lombok.Data;

@Data

public class ProjectDesignTableViewDto {
    
    private String projectName;

    private Long boqId;
    
    private List<ProjectDesignTableDto> projectDesigns;
}
