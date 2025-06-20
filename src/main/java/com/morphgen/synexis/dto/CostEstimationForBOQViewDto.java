package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.BoqStatus;

import lombok.Data;

@Data

public class CostEstimationForBOQViewDto {
    
    private Long JobId;

    private BoqStatus boqStatus;

    private List<BoqItemViewDto> items;
    
}
