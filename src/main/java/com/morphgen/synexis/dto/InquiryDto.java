package com.morphgen.synexis.dto;

import java.time.LocalDate;

import com.morphgen.synexis.enums.InquiryType;
import com.morphgen.synexis.enums.ProjectType;

import lombok.Data;

@Data

public class InquiryDto {
    
    private String projectName;

    private LocalDate projectReturnDate;

    private InquiryType inquiryType;

    private ProjectType projectType;

    private Long customerId;

    private Long salesPersonId;

    private Long estimatorId;
    
}
