package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data

public class BoqItemMaterialViewDto {
    
    private Long itemMaterialId;
    
    private Long materialId;

    private String materialName;

    private BigDecimal materialQuantity;

    private String materialDescription;

    private Integer sectionId;
    
}
 