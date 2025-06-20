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

public class BoqItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String itemName;

    private BigDecimal itemQuantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "boqId")
    private BillOfQuantities billOfQuantities;

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<BoqItemMaterial> materials;
}
