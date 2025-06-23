package com.morphgen.synexis.dto;

import java.util.List;

import com.morphgen.synexis.enums.BoqStatus;

import lombok.Data;

@Data

public class CustomerDesignAssetViewDto {
    
    private BoqStatus boqStatus;

    private Long jobId;

    List<CustomerDesignViewDto> customerDesignViewDtoList;
    
}
