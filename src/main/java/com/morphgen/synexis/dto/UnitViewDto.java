package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class UnitViewDto {
    
    private Long unitId;
    
    private String unitName;

    private String UnitShortName;

    private Boolean unitAllowDecimal;

    private Status unitStatus;

    private List<AssociatedMaterialDto> associatedMaterialList;

}
