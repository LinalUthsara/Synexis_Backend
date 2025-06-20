package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.BoqStatus;

import lombok.Data;

@Data

public class BoqDto {
    
    private Long JobId;

    private BoqStatus boqStatus;

    private List<BoqItemDto> items;
    
}
