package com.morphgen.synexis.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.DesignStatus;

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

public class ProjectDesign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectDesignId;

    @Enumerated(EnumType.STRING)
    private DesignStatus projectDesignStatus;

    private String projectDesignVersion;

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
    @ManyToOne
    @JoinColumn(name = "boqId")
    private BillOfQuantities billOfQuantities;

    @OneToMany(mappedBy = "projectDesign", cascade = CascadeType.ALL)
    private List<Design> designs;

}
