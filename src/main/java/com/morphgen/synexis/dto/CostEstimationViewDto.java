package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.util.List;

import com.morphgen.synexis.enums.EstimationStatus;

import lombok.Data;

@Data

public class CostEstimationViewDto {
    
    private String quotationVersion;
    
    private Long inquiryId;

    private BigDecimal labourRate;

    private BigDecimal otherCostRate;

    private EstimationStatus estimationStatus;
    
    private List<ItemViewDto> items;
    
}
