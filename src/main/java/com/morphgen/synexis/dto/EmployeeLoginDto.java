package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class EmployeeLoginDto {
    
    private Long employeeId;

    private String employeeName;

    private String roleName;

    private Boolean isPasswordChanged;
}
