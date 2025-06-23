package com.morphgen.synexis.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ProjectDesignUrlUtil {
    
    public static String constructProjectDesignUrl(Long designId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/projectDesign/viewDesign/")
            .path(String.valueOf(designId))
            .toUriString();
    }

}
