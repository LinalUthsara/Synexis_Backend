package com.morphgen.synexis.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data

public class ItemMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemMaterialId;

    private BigDecimal materialQuantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "itemId")
    private Item item;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "materialId")
    private Material material;
}
