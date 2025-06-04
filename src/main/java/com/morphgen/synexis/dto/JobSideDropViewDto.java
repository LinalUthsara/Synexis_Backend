package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.ProjectType;

import lombok.Data;

@Data

public class JobSideDropViewDto {
    
    private Long jobId;

    private String projectName;

    private String quotationVersion;

    private ProjectType projectType;
}
