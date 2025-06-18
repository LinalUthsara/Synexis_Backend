package com.morphgen.synexis.dto;

import java.time.LocalDateTime;

import com.morphgen.synexis.enums.NotificationType;

import lombok.Data;

@Data

public class NotificationPanelViewDto {
    
    private Long notificationId;

    private NotificationType notificationType;

    private String notificationTitle;

    private String notificationMessage;

    private LocalDateTime notificationCreatedAt;
    
    private Boolean notificationIsRead;
    
}
