package com.morphgen.synexis.dto;

import java.math.BigDecimal;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data
 
public class MaterialTableViewDto {
    
    private Long materialId;

    private String materialName;

    private String materialDescription;

    private String materialSKU;

    private BigDecimal materialPurchasePrice;

    private BigDecimal quantityInHand;

    private Status materialStatus;

    private String materialImageUrl;

}
