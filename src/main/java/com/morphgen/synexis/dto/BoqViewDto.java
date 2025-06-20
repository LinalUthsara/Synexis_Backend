package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.BoqStatus;

import lombok.Data;

@Data

public class BoqViewDto {

    private String boqVersion;
    
    private Long JobId;

    private BoqStatus boqStatus;

    private List<BoqItemViewDto> items;

}
