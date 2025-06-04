package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.Color;
import com.morphgen.synexis.enums.PaintingThickness;
import com.morphgen.synexis.enums.PowderCoating;
import com.morphgen.synexis.enums.SheetMaterial;
import com.morphgen.synexis.enums.SurfaceType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data

public class MaterialFinish {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialFinishId;

    private SheetMaterial sheetMaterial;

    private PaintingThickness paintingThickness;

    private Boolean primer;

    private PowderCoating powderCoating;

    private SurfaceType surfaceType;

    private Boolean HDG;

    private Color color;

    private String customColor;

    @JsonIgnore
    @OneToOne(mappedBy = "materialFinish")
    private TechnicalSpecification specification;

}
