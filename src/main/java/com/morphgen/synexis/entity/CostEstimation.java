package com.morphgen.synexis.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data

public class CostEstimation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estimationId;

    private String quotationNumber;

    private BigDecimal labourRate;

    @JsonIgnore
    @OneToMany(mappedBy = "costEstimation", cascade = CascadeType.ALL)
    private List<Item> items;
}
