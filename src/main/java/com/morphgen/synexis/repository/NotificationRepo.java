package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Notification;

@Repository

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    
}
