package com.morphgen.synexis.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import com.morphgen.synexis.enums.InvoiceType;
import com.morphgen.synexis.enums.JobStatus;
import com.morphgen.synexis.enums.PaymentType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data

public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private LocalTime jobDeliveryTime;
    
    private String jobDeliveryPoint;

    private String consultant;

    private String contractor;

    private String subContractor;

    private BigDecimal grossProfit;

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    @PrePersist
    protected void onCreate(){
        this.jobStatus = JobStatus.PENDING;
    }

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "job_invoice_types", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "invoice_type")
    private List<InvoiceType> invoiceType;

    private String customInvoiceType;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "specificationId")
    private TechnicalSpecification specification;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estimationId")
    private CostEstimation estimation;

}
