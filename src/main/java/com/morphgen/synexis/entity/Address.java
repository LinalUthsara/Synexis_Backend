package com.morphgen.synexis.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data

public class Address {
    
    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipCode;
}
