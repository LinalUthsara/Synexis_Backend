package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class TechnicalSpecificationDto {
    
    private Long specificationId;

    private Boolean floorMounting;

    private Boolean wallMounting;

    private String remarks;

    private FloorDimensionDto floorDimensionDto;

    private WallDimensionDto wallDimensionDto;

    private EnclosureDto enclosureDto;

    private CableBusbarDto cableBusbarDto;

    private MaterialFinishDto materialFinishDto;
}
