package com.morphgen.synexis.service.serviceImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.NotificationPanelViewDto;
import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.entity.EmployeeNotification;
import com.morphgen.synexis.entity.Notification;
import com.morphgen.synexis.enums.NotificationType;
import com.morphgen.synexis.exception.NotificationNotFoundException;
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

        List<String> privilegeNames = Arrays.asList(
            "ACTIVITY_LOG_VIEW",
            "EMPLOYEE_CREATE", "EMPLOYEE_VIEW", "EMPLOYEE_UPDATE", "EMPLOYEE_DELETE", "EMPLOYEE_REACTIVATE",
            "BRAND_CREATE", "BRAND_VIEW", "BRAND_UPDATE", "BRAND_DELETE", "BRAND_REACTIVATE",
            "CATEGORY_CREATE", "CATEGORY_VIEW", "CATEGORY_UPDATE", "CATEGORY_DELETE", "CATEGORY_REACTIVATE",
            "UNIT_CREATE", "UNIT_VIEW", "UNIT_UPDATE", "UNIT_DELETE", "UNIT_REACTIVATE",
            "MATERIAL_CREATE", "MATERIAL_VIEW", "MATERIAL_UPDATE", "MATERIAL_DELETE", "MATERIAL_REACTIVATE",
            "CUSTOMER_CREATE", "CUSTOMER_VIEW", "CUSTOMER_UPDATE", "CUSTOMER_DELETE", "CUSTOMER_REACTIVATE",
            "INQUIRY_CREATE", "INQUIRY_VIEW", "INQUIRY_UPDATE", "INQUIRY_DELETE", "INQUIRY_REACTIVATE",
            "ESTIMATION_CREATE", "ESTIMATION_VIEW", "ESTIMATION_APPROVAL_VIEW", "ESTIMATION_UPDATE", "ESTIMATION_APPROVE",
            "JOB_CREATE", "JOB_VIEW", "JOB_UPDATE", "JOB_DELETE", "JOB_APPROVE"
        );

        List<String> allowedActions = Arrays.asList("CREATE", "VIEW", "UPDATE", "DELETE", "APPROVE");

        privilegeNames.stream()
            .filter(priv -> allowedActions.stream().anyMatch(priv::endsWith))
            .forEach(priv -> {
                String[] parts = priv.split("_");
                if (parts.length >= 2) {
                    String subject = parts[0];
                    privilegeToNotificationSubject.put(priv, subject);
                }
            });
        
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

    @Override
    public List<NotificationPanelViewDto> getNotificationsForEmployee(Long employeeId) {
        
        List<EmployeeNotification> employeeNotifications = employeeNotificationRepo.findByEmployeeEmployeeId(employeeId);

        List<NotificationPanelViewDto> notificationPanelViewDtoList = employeeNotifications.stream().map(employeeNotification ->{

            NotificationPanelViewDto notificationPanelViewDto = new NotificationPanelViewDto();

            notificationPanelViewDto.setNotificationId(employeeNotification.getEmpNotifId());
            notificationPanelViewDto.setNotificationTitle(employeeNotification.getNotification().getNotificationTitle());
            notificationPanelViewDto.setNotificationMessage(employeeNotification.getNotification().getNotificationMessage());
            notificationPanelViewDto.setNotificationType(employeeNotification.getNotification().getNotificationType());
            notificationPanelViewDto.setNotificationCreatedAt(employeeNotification.getNotification().getNotificationCreatedAt());
            notificationPanelViewDto.setNotificationIsRead(employeeNotification.getNotificationIsRead());

            return notificationPanelViewDto;

        }).collect(Collectors.toList());

        return notificationPanelViewDtoList;
    }

    @Override
    public void markNotificationAsRead(Long empNotifId) {
        
        EmployeeNotification empNotif = employeeNotificationRepo.findById(empNotifId)
        .orElseThrow(() -> new NotificationNotFoundException("Notification not found!"));

        empNotif.setNotificationIsRead(true);

        employeeNotificationRepo.save(empNotif);
    }

    @Override
    public void markAllNotificationsAsRead(Long employeeId) {
        
        List<EmployeeNotification> empNotifs = employeeNotificationRepo.findByEmployeeEmployeeId(employeeId);
        
        for (EmployeeNotification notif : empNotifs) {
            
            notif.setNotificationIsRead(true);
        }

        employeeNotificationRepo.saveAll(empNotifs);
    }
}
