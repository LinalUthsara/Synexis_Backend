package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data

public class FloorDimensionDto {
    
    private Long floorDimensionId;

    private BigDecimal floorFramework;

    private BigDecimal floorBaseFrame;

    private BigDecimal floorPartition;

    private BigDecimal floorDoor;

    private BigDecimal floorMountingPlate;

    private BigDecimal floorEscutcheon;

    private BigDecimal floorCoveringPanels;

    private BigDecimal floorTopCover;

    private String floorTopCoverMaterial;

    private BigDecimal floorBottomPlate;
    
    private String floorBottomPlateMaterial;
    
}
