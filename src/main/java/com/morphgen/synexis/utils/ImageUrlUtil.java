package com.morphgen.synexis.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ImageUrlUtil {
    
    public static String constructImageUrl(Long brandId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/brand/image/")
            .path(String.valueOf(brandId))
            .toUriString();
    }

    public static String constructMaterialImageUrl(Long materialId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/material/image/")
            .path(String.valueOf(materialId))
            .toUriString();
    }

    public static String constructEmployeeImageUrl(Long employeeId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/employee/image/")
            .path(String.valueOf(employeeId))
            .toUriString();
    }
       
}
