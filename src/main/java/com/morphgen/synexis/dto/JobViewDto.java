package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.util.List;

import com.morphgen.synexis.enums.InvoiceType;
import com.morphgen.synexis.enums.JobStatus;
import com.morphgen.synexis.enums.PaymentType;
import com.morphgen.synexis.enums.ProjectType;

import lombok.Data;

@Data

public class JobViewDto {
    
    private Long jobId;

    private String projectName;

    private String quotationVersion;

    private String salesPersonName;

    private String estimatorName;

    private String customerName;

    private String customerAddress;

    private ProjectType projectType;

    private JobStatus jobStatus;

    private String jobNumber;

    private String jobDeliveryDate;

    private String jobDeliveryTime;
    
    private String jobDeliveryPoint;

    private String consultant;

    private String contractor;

    private String subContractor;

    private BigDecimal grossProfit;

    private PaymentType paymentType;

    private List<InvoiceType> invoiceType;

    private String customInvoiceType;

    private List<AttachmentViewDto> attachmentDtoList;

    private TechnicalSpecificationDto specificationDto;

    private Long estimationId;

}
