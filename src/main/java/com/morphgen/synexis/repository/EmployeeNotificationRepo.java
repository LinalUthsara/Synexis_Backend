package com.morphgen.synexis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.EmployeeNotification;

@Repository

public interface EmployeeNotificationRepo extends JpaRepository<EmployeeNotification, Long> {
    
    List<EmployeeNotification> findByEmployeeEmployeeId(Long employeeId);
}
