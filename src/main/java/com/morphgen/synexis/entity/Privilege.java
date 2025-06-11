package com.morphgen.synexis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Privilege {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilegeId;

    private String privilegeName;
    
    public Privilege(String privilegeName) {
    this.privilegeName = privilegeName;
    }

}
