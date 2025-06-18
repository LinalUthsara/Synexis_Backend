package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.NotificationPanelViewDto;
import com.morphgen.synexis.entity.Notification;
import com.morphgen.synexis.enums.NotificationType;

@Service

public interface NotificationService {
    
    Notification createNotification(String notificationTitle, String notificationMessage, NotificationType notificationType, String notificationSubject);

    List<NotificationPanelViewDto> getNotificationsForEmployee(Long employeeId);

    void markNotificationAsRead(Long empNotifId);
    void markAllNotificationsAsRead(Long employeeId);
}
