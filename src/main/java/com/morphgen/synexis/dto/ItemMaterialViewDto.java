package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import com.morphgen.synexis.enums.MaterialType;

import lombok.Data;

@Data

public class ItemMaterialViewDto {
    
    private Long itemMaterialId;
    
    private Long materialId;

    private String materialName;

    private BigDecimal materialQuantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    private String materialDescription;

    private String materialPartNumber;

    private BigDecimal materialMarketPrice; 

    private MaterialType materialType;

    private Integer sectionId;

    private String materialCountry;
}
