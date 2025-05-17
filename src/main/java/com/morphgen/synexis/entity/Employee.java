package com.morphgen.synexis.entity;

import java.time.LocalDate;

import com.morphgen.synexis.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data

public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String employeePrefix;

    private String employeeFirstName;

    private String employeeLastName;

    private String employeeNIC;

    private LocalDate employeeDOB;

    @Lob
    private byte[] employeeImage;

    private String employeeEmail;

    private String employeePhoneNumber;

    private Address employeeAddress;

    private String Role;

    private Status employeeStatus;

    @PrePersist
    protected void onCreate(){
        this.employeeStatus = Status.ACTIVE;
    }

}
