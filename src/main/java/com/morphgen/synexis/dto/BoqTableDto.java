package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.BoqStatus;

import lombok.Data;

@Data

public class BoqTableDto {
    
    private Long boqId;

    private String boqVersion;

    private BoqStatus boqStatus;

    private String lastModifiedDate;

    private Boolean customerDesignPresent;

}
