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

@Entity
@Data

public class BrandImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandImageId;

    private String brandImageName;

    private String brandImageType;

    private Long brandImageSize;

    @Lob
    private byte[] brandImageData;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "brandId")
    private Brand brand;
}
