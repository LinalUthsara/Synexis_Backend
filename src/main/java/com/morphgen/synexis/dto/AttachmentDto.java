package com.morphgen.synexis.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

public class AttachmentDto {
    
    private Long attachmentId;

    private MultipartFile attachment;
    
}
