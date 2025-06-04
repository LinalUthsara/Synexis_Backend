package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class CustomerSideDropViewDto {
    
    private Long customerId;

    private String customerPrefix;

    private String customerFirstName;

    private String customerLastName;

    private String customerEmail;

}
