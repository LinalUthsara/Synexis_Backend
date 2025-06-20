package com.morphgen.synexis.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class CustomerDesignUrlUtil {
    
    public static String constructCustomerDesignUrl(Long cdesignId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/boq/viewCustomerDesign/")
            .path(String.valueOf(cdesignId))
            .toUriString();
    }

}
