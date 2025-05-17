package com.morphgen.synexis.entity;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.Status;

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
import lombok.Data;

@Entity
@Data

public class Unit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    private String unitName;

    private String unitShortName;

    private Boolean unitAllowDecimal;

    @Enumerated(EnumType.STRING)
    private Status unitStatus;

    @PrePersist
    protected void onCreate(){
        this.unitStatus = Status.ACTIVE;
        
        if(unitAllowDecimal == null){
            unitAllowDecimal = true;
        }
    }

    private Double unitConversionFactor;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "baseUnitId")
    private Unit baseUnit;

    @JsonIgnore
    @OneToMany(mappedBy = "baseUnit", cascade = CascadeType.ALL)
    private Set<Unit> derivedUnits;

    @JsonIgnore
    @OneToMany(mappedBy = "baseUnit")
    private List<Material> baseUnitMaterials;

    @JsonIgnore
    @OneToMany(mappedBy = "otherUnit")
    private List<Material> otherUnitMaterials;

}