package com.morphgen.synexis.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data

public class FloorDimension {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonIgnore
    @OneToOne(mappedBy = "floorDimension")
    private TechnicalSpecification specification;

}
