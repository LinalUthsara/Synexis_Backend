package com.morphgen.synexis.dto;

import java.util.List;

import lombok.Data;

@Data

public class CostEstimationTableViewDto {
    
    private Long inquiryId;

    private String quotationNumber;

    private List<CostEstimationTableDto> estimations;
    
}
