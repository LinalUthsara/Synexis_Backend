package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BrandUpdateDto {
    
    private Long brandId;
    
    private String brandName;

    private String brandDescription;

    private String brandCountry;

    private String brandWebsite;

    @Lob
    private byte[] brandImage;

    private Status brandStatus;
}
