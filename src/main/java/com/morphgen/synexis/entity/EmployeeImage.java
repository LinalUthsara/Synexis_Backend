package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data

public class EmployeeImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeImageId;

    private String employeeImageName;

    private String employeeImageType;

    private Long employeeImageSize;

    @Lob
    private byte[] employeeImageData;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;

}
