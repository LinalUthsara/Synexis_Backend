package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Color;
import com.morphgen.synexis.enums.PaintingThickness;
import com.morphgen.synexis.enums.PowderCoating;
import com.morphgen.synexis.enums.SheetMaterial;
import com.morphgen.synexis.enums.SurfaceType;

import lombok.Data;

@Data

public class MaterialFinishDto {
    
    private Long materialFinishId;
    
    private SheetMaterial sheetMaterial;

    private PaintingThickness paintingThickness;

    private Boolean primer;

    private PowderCoating powderCoating;

    private SurfaceType surfaceType;

    private Boolean HDG;

    private Color color;

    private String customColor;
    
}
