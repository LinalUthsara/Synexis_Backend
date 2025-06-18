package com.morphgen.synexis.entity;

import java.time.LocalDateTime;

import com.morphgen.synexis.enums.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data

public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String notificationTitle;

    private String notificationMessage;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String notificationSubject;

    private LocalDateTime notificationCreatedAt;

    @PrePersist
    protected void onCreate(){
        this.notificationCreatedAt = LocalDateTime.now();
    }

}
