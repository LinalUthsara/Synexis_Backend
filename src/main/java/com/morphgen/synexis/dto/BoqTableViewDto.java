package com.morphgen.synexis.dto;

import java.util.List;

import lombok.Data;

@Data

public class BoqTableViewDto {
    
    private Long JobId;

    private String quotationVersion;

    private List<BoqTableDto> boqs;

}
