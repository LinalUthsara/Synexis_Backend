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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;

    private String categoryDescription;

    @Enumerated(EnumType.STRING)
    private Status categoryStatus;

    @PrePersist
    protected void onCreate(){
        this.categoryStatus = Status.ACTIVE;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parentCategoryId")
    private Category parentCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private Set<Category> subCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Material> categoryMaterials;

    @JsonIgnore
    @OneToMany(mappedBy = "subCategory")
    private List<Material> subCategoryMaterials;

}
