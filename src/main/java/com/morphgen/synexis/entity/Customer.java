package com.morphgen.synexis.entity;

import java.util.List;

import com.morphgen.synexis.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String customerPrefix;

    private String customerFirstName;

    private String customerLastName;

    private String customerEmail;

    private String customerPhoneNumber;

    private Address customerAddress;

    @Enumerated(EnumType.STRING)
    private Status customerStatus;

    @PrePersist
    protected void onCreate(){
        this.customerStatus = Status.ACTIVE;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<File> files;
}
