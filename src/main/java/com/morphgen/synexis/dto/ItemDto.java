package com.morphgen.synexis.dto;

import java.util.List;

import lombok.Data;

@Data

public class ItemDto {
    
    private String itemName;

    private Long itemQuantity;

    private List<ItemMaterialDto> itemMaterials;

}
