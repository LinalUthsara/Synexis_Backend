package com.morphgen.synexis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UnitUpdateDto {

    private Long unitId;
    
    private String unitName;

    private String UnitShortName;

    private Boolean unitAllowDecimal;

    private String baseUnitName;

    private Double unitConversionFactor;

}
