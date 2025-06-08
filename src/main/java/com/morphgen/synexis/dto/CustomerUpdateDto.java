package com.morphgen.synexis.dto;

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

public class CustomerUpdateDto {
 
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

    @Lob
    private byte[] fileBRC;

    @Lob
    private byte[] fileVAT;

    @Lob
    private byte[] fileSVAT;

    private Status customerStatus;

}