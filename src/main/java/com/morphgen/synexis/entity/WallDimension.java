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


public class WallDimension {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonIgnore
    @OneToOne(mappedBy = "wallDimension")
    private TechnicalSpecification specification;

}
