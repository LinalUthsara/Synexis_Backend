package com.morphgen.synexis.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

public class CustomerDto {
    
    private String customerPrefix;

    private String customerFirstName;

    private String customerLastName;

    private String customerEmail;

    private String customerPhoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipCode;

    private MultipartFile BRC;

    private MultipartFile VAT;

    private MultipartFile SVAT;
    
}
