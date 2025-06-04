package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.JobStatus;

import lombok.Data;

@Data

public class JobTableViewDto {
    
    private Long jobId;

    private String customerName;

    private String projectName;

    private String quotationVersion;

    private JobStatus jobStatus;

    private String jobReturnDate;
}
