package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.util.List;

import com.morphgen.synexis.enums.EstimationStatus;

import lombok.Data;

@Data

public class CostEstimationDto {
    
    private Long inquiryId;

    private EstimationStatus estimationStatus;
    
    private List<ItemDto> items;

    private BigDecimal labourRate;

    private BigDecimal otherCostRate;
    
}
