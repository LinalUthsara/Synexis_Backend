package com.morphgen.synexis.dto;

import java.time.LocalDate;

import com.morphgen.synexis.enums.InquiryType;
import com.morphgen.synexis.enums.ProjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InquiryUpdateDto {
    
    private String projectName;

    private LocalDate projectReturnDate;

    private InquiryType inquiryType;

    private ProjectType projectType;

    private String customerName;

    private String estimatorName;

    private String salesPersonName;

}
