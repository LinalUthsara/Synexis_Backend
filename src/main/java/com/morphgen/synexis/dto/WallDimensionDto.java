package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data

public class WallDimensionDto {
    
    private Long wallDimensionId;

    private BigDecimal wallLid;

    private BigDecimal wallShelf;

    private BigDecimal wallDoor;

    private BigDecimal wallCoverPlate;

    private BigDecimal wallMountingPlate;

    private BigDecimal wallTopGlandPlate;

    private String wallTopGlandPlateMaterial;

    private BigDecimal wallBottomGlandPlate;
    
    private String wallBottomGlandPlateMaterial;
    
}
