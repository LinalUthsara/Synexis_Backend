package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data

public class CustomerDesign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cDesignId;

    private String cDesignname;

    private String cDesignType;

    private Long cDesignSize;

    @Lob
    private byte[] cDesignData;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "boqId")
    private BillOfQuantities billOfQuantities;
}
