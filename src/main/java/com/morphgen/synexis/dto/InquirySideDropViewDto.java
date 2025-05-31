package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.InquiryType;

import lombok.Data;

@Data

public class InquirySideDropViewDto {
    
    private Long inquiryId;

    private String projectName;

    private String quotationNumber;

    private InquiryType inquiryType;
    
}
