package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity

public class MaterialImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialImageId;

    private String materialImageName;

    private String materialImageType;

    private Long materialImageSize;

    @Lob
    private byte[] materialImageData;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "materialId")
    private Material material;
}
