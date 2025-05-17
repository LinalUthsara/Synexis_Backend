package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import com.morphgen.synexis.enums.InventoryType;
import com.morphgen.synexis.enums.MaterialType;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MaterialUpdateDto {

    private Long materialId;
    
    private String materialName;

    private String materialDescription;

    private String materialPartNumber;

    private String materialSKU;

    @Lob
    private byte[] materialImage;

    private BigDecimal materialMarketPrice;

    private BigDecimal materialPurchasePrice;

    private BigDecimal alertQuantity;

    private Boolean materialForUse;

    private MaterialType materialType;

    private InventoryType materialInventoryType;

    private String brandName;

    private String categoryName;

    private String subCategoryName;

    private String baseUnitName;

    private String otherUnitName;
}

// .materialId
// .materialName
// .materialDescription
// .materialPartNumber
// .materialSKU
// .materialImage
// .materialMarketPrice
// .materialPurchasePrice
// .alertQuantity
// .materialForUse
// .materialType
// .materialInventoryType
// .brandName
// .categoryName
// .subCategoryName
// .baseUnitName
// .otherUnitName
