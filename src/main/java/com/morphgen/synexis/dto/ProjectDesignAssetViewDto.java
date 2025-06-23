package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.DesignStatus;

import lombok.Data;

@Data

public class ProjectDesignAssetViewDto {
    
    private Long projectDesignId;

    private Long boqId;

    private DesignStatus projectDesignStatus;

    List<DesignViewDto> designViewList;
    
}
