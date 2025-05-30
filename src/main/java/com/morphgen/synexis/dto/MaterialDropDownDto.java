package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import com.morphgen.synexis.enums.MaterialType;

import lombok.Data;

@Data

public class MaterialDropDownDto {
    
    private Long materialId;

    private String materialName;

    private String materialDescription;

    private String materialMake;

    private String materialCountry;

    private String materialPartNumber;

    private BigDecimal materialMarketPrice;

    private MaterialType materialType;

}
