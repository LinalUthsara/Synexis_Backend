package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data

public class TechnicalSpecification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long specificationId;

    private Boolean floorMounting;

    private Boolean wallMounting;

    private String remarks;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "floorDimensionId")
    private FloorDimension floorDimension;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallDimensionId")
    private WallDimension wallDimension;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "enclosureId")
    private Enclosure enclosure;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cableBusbarId")
    private CableBusbar cableBusbar;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "materialFinishId")
    private MaterialFinish materialFinish;

    @JsonIgnore
    @OneToOne(mappedBy = "specification")
    private Job job;
}

