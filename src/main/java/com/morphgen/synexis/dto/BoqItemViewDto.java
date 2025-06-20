package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data

public class BoqItemViewDto {
    
    private Long itemId;

    private String itemName;

    private BigDecimal itemQuantity;

    private List<BoqItemMaterialViewDto> itemMaterials;

}
