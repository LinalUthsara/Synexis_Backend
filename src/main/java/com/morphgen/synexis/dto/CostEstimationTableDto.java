package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.EstimationStatus;

import lombok.Data;

@Data

public class CostEstimationTableDto {
    
    private Long estimationId;

    private String quotationVersion;

    private EstimationStatus estimationStatus;

    private String lastModifiedDate;
    
}
