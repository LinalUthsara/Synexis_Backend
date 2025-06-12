package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.entity.Notification;
import com.morphgen.synexis.enums.NotificationType;

@Service

public interface NotificationService {
    
    Notification createNotification(String notificationTitle, String notificationMessage, NotificationType notificationType, String notificationSubject);
}
