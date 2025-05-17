package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class EmployeeTableViewDto {
    
    private Long employeeId;

    private String employeeName;

    private String employeeImageUrl;

    private String employeeEmail;

    private String employeePhoneNumber;

    private String Role;

    private Status employeeStatus;
}
