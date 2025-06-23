package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.DesignStatus;

import lombok.Data;

@Data

public class ProjectDesignUpdateDto {
    
    private DesignStatus designStatus;
    
    private List<DesignDto> designs;

}
