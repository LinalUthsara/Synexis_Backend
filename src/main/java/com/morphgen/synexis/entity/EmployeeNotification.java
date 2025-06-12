package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data

public class EmployeeNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empNotifId;

    private Boolean notificationIsRead;

    @PrePersist
    protected void onCreate(){
        this.notificationIsRead = false;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "notificationId")
    private Notification notification;
}
