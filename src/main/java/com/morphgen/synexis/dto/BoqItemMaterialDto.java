package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data

public class BoqItemMaterialDto {
    
    private Long itemMaterialId;
    
    private Long materialId;

    private BigDecimal materialQuantity;


}
