package com.morphgen.synexis.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

public class EmployeeDto {
    
    private String employeePrefix;

    private String employeeFirstName;

    private String employeeLastName;

    private String employeeNIC;

    private LocalDate employeeDOB;

    private String employeeGender;

    private MultipartFile employeeImage;

    private String employeeEmail;

    private String employeePhoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipCode;

    private Long roleId;
}
