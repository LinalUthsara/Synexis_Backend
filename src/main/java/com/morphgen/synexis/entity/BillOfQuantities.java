package com.morphgen.synexis.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.BoqStatus;

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

public class BillOfQuantities {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boqId;

    @Enumerated(EnumType.STRING)
    private BoqStatus boqStatus;

    private String boqVersion;

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
    @OneToMany(mappedBy = "billOfQuantities", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoqItem> items;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "jobId")
    private Job job;

    @OneToMany(mappedBy = "billOfQuantities", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerDesign> customerDesigns;

}
