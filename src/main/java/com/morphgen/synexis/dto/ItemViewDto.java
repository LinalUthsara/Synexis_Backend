package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data

public class ItemViewDto {
    
    private Long itemId;
    
    private String itemName;

    private BigDecimal itemQuantity;

    private BigDecimal switchGearComponentMarkup;

    private BigDecimal controlAccessoryMarkup;
    
    private BigDecimal busBarMarkup;

    private BigDecimal wiringMarkup;

    private BigDecimal otherAccessoryMarkup;

    private BigDecimal electricalLabourMarkup;

    private BigDecimal transportMarkup;
    
    private BigDecimal enclosureMarkup;

    private List<ItemMaterialViewDto> itemMaterials;
    
}
