package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class InquiryTableViewDto {
    
    private Long inquiryId;

    private String quotationNumber;

    private String projectName;

    private String customerName;

    private Status inquiryStatus;
    
}
