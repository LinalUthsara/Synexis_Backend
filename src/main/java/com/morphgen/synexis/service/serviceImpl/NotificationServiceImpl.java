package com.morphgen.synexis.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.entity.EmployeeNotification;
import com.morphgen.synexis.entity.Notification;
import com.morphgen.synexis.enums.NotificationType;
import com.morphgen.synexis.repository.EmployeeNotificationRepo;
import com.morphgen.synexis.repository.EmployeeRepo;
import com.morphgen.synexis.repository.NotificationRepo;
import com.morphgen.synexis.service.NotificationService;

import jakarta.annotation.PostConstruct;

@Service

public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired 
    private EmployeeRepo employeeRepo;

    @Autowired
    private EmployeeNotificationRepo employeeNotificationRepo;

    private Map<String, String> privilegeToNotificationSubject;

    @PostConstruct
    public void initPrivilegeToNotificationType() {
        privilegeToNotificationSubject = new HashMap<>();
        privilegeToNotificationSubject.put("CREATE_BRAND", "BRAND");
        privilegeToNotificationSubject.put("CREATE_CATEGORY", "CATEGORY");
        privilegeToNotificationSubject.put("CREATE_MATERIAL", "MATERIAL");
        privilegeToNotificationSubject.put("CREATE_ESTIMATION", "ESTIMATION");
        
    }

    @Override
    public Notification createNotification(String notificationTitle, String notificationMessage, NotificationType notificationType, String notificationSubject) {
        
        Notification notification = new Notification();

        notification.setNotificationTitle(notificationTitle);
        notification.setNotificationMessage(notificationMessage);
        notification.setNotificationType(notificationType);
        notification.setNotificationSubject(notificationSubject);

        Notification savedNotification = notificationRepo.save(notification);

        List<String> relevantPrivileges = privilegeToNotificationSubject.entrySet().stream()
        .filter(e -> e.getValue().equals(notificationSubject))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

        List<Employee> employees = employeeRepo.findByPrivileges(relevantPrivileges);

        for (Employee emp : employees) {
            EmployeeNotification employeeNotification = new EmployeeNotification();
            employeeNotification.setEmployee(emp);
            employeeNotification.setNotification(savedNotification);
            employeeNotificationRepo.save(employeeNotification);
        }

        return savedNotification;
    }
}
