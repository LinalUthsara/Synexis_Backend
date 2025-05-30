package com.morphgen.synexis.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.morphgen.synexis.enums.InquiryType;
import com.morphgen.synexis.enums.ProjectType;
import com.morphgen.synexis.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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

public class Inquiry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    private String quotationNumber;

    private String projectName;

    private LocalDate projectReturnDate;

    private InquiryType inquiryType;

    private ProjectType projectType;

    private Status inquiryStatus;

    @PrePersist
    protected void onCreate(){
        this.inquiryStatus = Status.ACTIVE;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "salesPersonEmployeeId")
    private Employee salesPerson;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "estimatorEmployeeId")
    private Employee estimator;

    @JsonIgnore
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL)
    private List<CostEstimation> costEstimations;
}
