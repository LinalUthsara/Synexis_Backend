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

public class CableBusbar {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cableBusbarId;

    private Boolean BusbarIncoming;

    private Boolean BusbarOutgoing;

    private Boolean TopIncoming;

    private Boolean TopOutgoing;

    private Boolean BottomIncoming;

    private Boolean BottomOutgoing;

    private Boolean LeftIncoming;

    private Boolean LeftOutgoing;

    private Boolean RightIncoming;

    private Boolean RightOutgoing;

    private Boolean RearTopIncoming;

    private Boolean RearTopOutgoing;

    private Boolean RearBottomIncoming;
    
    private Boolean RearBottomOutgoing;

    @JsonIgnore
    @OneToOne(mappedBy = "cableBusbar")
    private TechnicalSpecification specification;

}
