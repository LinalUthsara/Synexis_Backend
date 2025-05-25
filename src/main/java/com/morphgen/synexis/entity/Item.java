package com.morphgen.synexis.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data

public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "estimationId")
    private CostEstimation costEstimation;

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemMaterial> materials;
}
