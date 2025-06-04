package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data

public class Enclosure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enclosureId;

    private Boolean surfaceTypeOutdoor;

    private Boolean surfaceTypeIndoor;

    private Boolean flushTypeOutdoor;

    private Boolean flushTypeIndoor;

    private Boolean freestandingTypeOutdoor;

    private Boolean freestandingTypeIndoor;

    private Boolean lidTypeOutdoor;

    private Boolean lidTypeIndoor;

    private Boolean outdoorWallOutdoor;

    private Boolean outdoorWallIndoor;

    private Boolean feederPillarOutdoor;
    
    private Boolean feederPillarIndoor;

    @JsonIgnore
    @OneToOne(mappedBy = "enclosure")
    private TechnicalSpecification specification;
}
