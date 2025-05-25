package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data

public class CostEstimationDto {
    
    private String quotationNumber;

    private BigDecimal labourRate;
    
    private List<ItemDto> items;
    
}
