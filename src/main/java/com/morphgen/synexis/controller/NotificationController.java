package com.morphgen.synexis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.NotificationPanelViewDto;
import com.morphgen.synexis.service.NotificationService;

@RestController
@RequestMapping("api/synexis/notification")
@CrossOrigin("*")

public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/panel/{employeeId}")
    public ResponseEntity<List<NotificationPanelViewDto>> getNotificationsForEmployee(@PathVariable Long employeeId){

        List<NotificationPanelViewDto> notificationPanelViewDtoList = notificationService.getNotificationsForEmployee(employeeId);

        return ResponseEntity.status(HttpStatus.OK).body(notificationPanelViewDtoList);

    }

    @PutMapping("/read/{empNotifId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long empNotifId) {

        notificationService.markNotificationAsRead(empNotifId);

        return ResponseEntity.ok("Notification marked as read");
    }

    @PutMapping("/readAll/{employeeId}")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long employeeId) {

        notificationService.markAllNotificationsAsRead(employeeId);
        
        return ResponseEntity.ok("All notifications marked as read");
    }
}
