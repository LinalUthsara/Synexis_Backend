package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class CustomerViewDto {
    
    private Long customerId;

    private String customerPrefix;

    private String customerFirstName;

    private String customerLastName;

    private String customerEmail;

    private String customerPhoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipCode;

    private String BRCDocUrl;

    private String VATDocUrl;

    private String SVATDocUrl;
    
}
