package com.morphgen.synexis.entity;

import java.time.LocalDate;

import com.morphgen.synexis.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String employeePrefix;

    private String employeeFirstName;

    private String employeeLastName;

    private String employeeNIC;

    private LocalDate employeeDOB;

    private String employeeGender;

    @Lob
    private byte[] employeeImage;

    private String employeeEmail;

    private String employeePhoneNumber;

    private Address employeeAddress;

    private String Role;

    @Enumerated(EnumType.STRING)
    private Status employeeStatus;

    @PrePersist
    protected void onCreate(){
        this.employeeStatus = Status.ACTIVE;
    }

}
