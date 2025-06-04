package com.morphgen.synexis.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class AttachmentUrlUtil {
    
    public static String constructAttachmentUrl(Long attachmentId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/synexis/job/attachment/")
            .path(String.valueOf(attachmentId))
            .toUriString();
    }
}
