package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.DesignStatus;

import lombok.Data;

@Data

public class ProjectDesignTableDto {
    
    private Long projectDesignId;

    private DesignStatus projectDesignStatus;

    private String projectDesignVersion;

    private String lastModifiedDate;
}
