package com.morphgen.synexis.dto;

import java.util.List;

import lombok.Data;

@Data

public class CostEstimationDto {
    
    private String quotationNumber;
    
    private List<ItemDto> items;
    
}
