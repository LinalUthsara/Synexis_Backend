package com.morphgen.synexis.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ImageUrlUtil {
    
    public static String constructImageUrl(Long brandId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/brand/image/")
            .path(String.valueOf(brandId))
            .toUriString();
    }
    
}
