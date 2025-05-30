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

    private String customerName;

    private String estimatorName;

    private String salesPersonName;

    private Status inquiryStatus;
    
}
