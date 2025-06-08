package com.morphgen.synexis.dto;

import java.time.LocalDate;

import com.morphgen.synexis.enums.Status;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeUpdateDto {
    
    private Long employeeId;

    private String employeePrefix;

    private String employeeFirstName;

    private String employeeLastName;

    private String employeeNIC;

    private LocalDate employeeDOB;

    private String employeeGender;

    @Lob
    private byte[] employeeImage;

    private String employeeEmail;

    private String employeePhoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipCode;

    private String Role;

    private Status employeeStatus;

}
