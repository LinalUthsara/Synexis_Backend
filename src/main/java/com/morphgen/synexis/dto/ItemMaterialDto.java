package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data

public class ItemMaterialDto {
    
    private Long materialId;

    private BigDecimal materialQuantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;
    
}
