package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import com.morphgen.synexis.enums.InventoryType;
import com.morphgen.synexis.enums.MaterialType;

import lombok.Data;

@Data

public class MaterialDto {
    
    private String materialName;

    private String materialDescription;

    private String materialPartNumber;

    private String materialMake;

    private String materialSKU;

    private MultipartFile materialImage;

    private BigDecimal materialMarketPrice;

    private BigDecimal materialPurchasePrice;

    private BigDecimal alertQuantity;

    private Boolean materialForUse;

    private MaterialType materialType;

    private InventoryType materialInventoryType;

    private Long brandId;

    private Long categoryId;

    private Long subCategoryId;

    private Long baseUnitId;

    private Long otherUnitId;
}
