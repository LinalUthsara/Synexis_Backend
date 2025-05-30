package com.morphgen.synexis.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.EstimationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data

public class CostEstimation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estimationId;

    private String quotationVersion;

    @Enumerated(EnumType.STRING)
    private EstimationStatus estimationStatus;

    private BigDecimal labourRate;

    private BigDecimal otherCostRate;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate(){
        this.updatedAt = LocalDateTime.now();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "costEstimation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "inquiryId")
    private Inquiry inquiry;
}
