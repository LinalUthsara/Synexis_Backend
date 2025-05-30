package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class UnitTableViewDto {
    
    private Long unitId;

    private String unitName;

    private String UnitShortName;

    private Boolean unitAllowDecimal;

    private Status unitStatus;

    private Long materialCount;
}
