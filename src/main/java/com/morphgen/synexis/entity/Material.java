package com.morphgen.synexis.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.InventoryType;
import com.morphgen.synexis.enums.MaterialType;
import com.morphgen.synexis.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data

public class Material {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;

    private String materialName;

    private String materialDescription;

    private String materialPartNumber;

    private String materialMake;

    private String materialSKU;

    private String materialBarcode;

    private BigDecimal materialMarketPrice;

    private BigDecimal materialPurchasePrice;

    private BigDecimal quantityInHand;

    private BigDecimal alertQuantity;

    private Boolean materialForUse;

    @Enumerated(EnumType.STRING)
    private Status materialStatus;

    @PrePersist
    protected void onCreate(){
        this.materialStatus = Status.ACTIVE;
        this.quantityInHand = BigDecimal.ZERO;
        if(materialForUse == null){
            materialForUse = true;
        }
    }

    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    @Enumerated(EnumType.STRING)
    private InventoryType materialInventoryType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brand brand;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subCategoryId")
    private Category subCategory;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "baseUnitId")
    private Unit baseUnit;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "otherUnitId")
    private Unit otherUnit;

    @JsonIgnore
    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    private List<ItemMaterial> itemMaterials;

    @OneToOne(mappedBy = "material", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MaterialImage materialImage;

}
