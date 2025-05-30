package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Status;

import lombok.Data;

@Data

public class CustomerTableViewDto {
    
    private Long customerId;

    private String customerName;

    private String customerEmail;

    private String customerPhoneNumber;

    private Status customerStatus;
}
