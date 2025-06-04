package com.morphgen.synexis.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import com.morphgen.synexis.enums.InvoiceType;
import com.morphgen.synexis.enums.PaymentType;

import lombok.Data;

@Data

public class JobDto {

    private LocalTime jobDeliveryTime;
    
    private String jobDeliveryPoint;

    private String consultant;

    private String contractor;

    private String subContractor;

    private BigDecimal grossProfit;
    
    private PaymentType paymentType;

    private List<InvoiceType> invoiceType;

    private String customInvoiceType;

    private List<AttachmentDto> attachments;

    private TechnicalSpecificationDto specificationDto;

    private Long estimationId;
    
}
