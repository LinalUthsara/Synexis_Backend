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

public class Design {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designId;

    private String designName;

    private String designType;

    private Long designSize;

    @Lob
    private byte[] designData;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "projectDesignId")
    private ProjectDesign projectDesign;

}
