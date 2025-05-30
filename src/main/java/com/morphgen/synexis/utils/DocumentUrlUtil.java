package com.morphgen.synexis.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.morphgen.synexis.enums.DocumentType;

public class DocumentUrlUtil {
    
    public static String constructCustomerDocumentUrl(Long customerId, DocumentType documentType) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/customer/")
            .path(String.valueOf(customerId))
            .path("/")
            .path(String.valueOf(documentType))
            .toUriString();
    }
}
