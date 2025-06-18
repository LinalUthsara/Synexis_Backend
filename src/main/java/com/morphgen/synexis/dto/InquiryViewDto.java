package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.InquiryType;
import com.morphgen.synexis.enums.ProjectType;
import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class InquiryViewDto {
    
    private Long inquiryId;

    private String quotationNumber;

    private String projectName;

    private String projectReturnDate;

    private InquiryType inquiryType;

    private ProjectType projectType;

    private Long customerId;

    private String customerName;

    private Long estimatorId;

    private String estimatorName;

    private Long salesPersonId;

    private String salesPersonName;

    private Status inquiryStatus;
    
}
