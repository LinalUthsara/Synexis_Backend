package com.morphgen.synexis.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Brand {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;

    private String brandName;

    private String brandDescription;

    private String brandCountry;

    private String brandWebsite;

    @OneToOne(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BrandImage brandImage;

    @Enumerated(EnumType.STRING)
    private Status brandStatus;

    @PrePersist
    protected void onCreate(){
        this.brandStatus = Status.ACTIVE;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "brand")
    private List<Material> materials;

}
