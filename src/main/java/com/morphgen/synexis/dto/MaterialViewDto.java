package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import com.morphgen.synexis.enums.InventoryType;
import com.morphgen.synexis.enums.MaterialType;
import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class MaterialViewDto {
    
    private Long materialId;

    private String materialName;

    private String materialDescription;

    private String materialPartNumber;

    private String materialSKU;

    private String materialBarcode;

    private String materialImageUrl;

    private BigDecimal materialMarketPrice;

    private BigDecimal materialPurchasePrice;

    private BigDecimal alertQuantity;

    private BigDecimal quantityInHand;

    private Boolean materialForUse;

    private MaterialType materialType;

    private InventoryType materialInventoryType;

    private String brandName;

    private String categoryName;

    private String subCategoryName;

    private String baseUnitName;

    private String otherUnitName;

    private Status materialStatus;

}
