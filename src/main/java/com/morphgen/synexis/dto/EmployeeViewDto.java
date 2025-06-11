package com.morphgen.synexis.dto;

import java.time.LocalDate;

import lombok.Data;

@Data

public class EmployeeViewDto {
    
    private Long employeeId;

    private String employeePrefix;

    private String employeeFirstName;

    private String employeeLastName;

    private String employeeNIC;

    private LocalDate employeeDOB;

    private String employeeGender;

    private String employeeImageUrl;

    private String employeeEmail;

    private String employeePhoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipCode;

    private String roleName;
}
