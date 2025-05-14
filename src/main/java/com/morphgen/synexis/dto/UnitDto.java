package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class UnitDto {
    
    private String unitName;

    private String UnitShortName;

    private Boolean unitAllowDecimal;

    private Long baseUnitId;

    private Double unitConversionFactor;
    
}
