package com.morphgen.synexis.entity;

import java.util.HashSet;
import java.util.Set;

import com.morphgen.synexis.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data

public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String roleName;

    @Enumerated(EnumType.STRING)
    private Status roleStatus;

    @PrePersist
    protected void onCreate(){
        this.roleStatus = Status.ACTIVE;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Privilege> privileges = new HashSet<>();
}
