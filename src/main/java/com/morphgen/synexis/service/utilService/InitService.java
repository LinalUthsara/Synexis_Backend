package com.morphgen.synexis.service.utilService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.entity.Privilege;
import com.morphgen.synexis.entity.Role;
import com.morphgen.synexis.repository.EmployeeRepo;
import com.morphgen.synexis.repository.PrivilegeRepo;
import com.morphgen.synexis.repository.RoleRepo;
import com.morphgen.synexis.service.EmployeeService;

import jakarta.annotation.PostConstruct;

@Service

public class InitService {
    
    @Autowired 
    private RoleRepo roleRepo;

    @Autowired 
    private PrivilegeRepo privilegeRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired 
    private EmployeeService employeeService;

    @PostConstruct
    @Transactional
    public void init() {

        try {
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

            List<Privilege> existingPrivileges = privilegeRepo.findAll();
            List<String> existingPrivilegeNames = existingPrivileges.stream()
                .map(Privilege::getPrivilegeName)
                .toList();

            for (String privilegeName : privilegeNames) {
                if (!existingPrivilegeNames.contains(privilegeName)) {
                    privilegeRepo.saveAndFlush(new Privilege(privilegeName));
                }
            }

            String roleName = "ADMINISTRATOR";
            Role adminRole;
            Optional<Role> existingRole = roleRepo.findByRoleName(roleName);
            if (existingRole.isEmpty()) {
                adminRole = new Role();
                adminRole.setRoleName(roleName);
                adminRole.setPrivileges(new HashSet<>(privilegeRepo.findAll()));
                roleRepo.saveAndFlush(adminRole);
            }
            else{
                adminRole = existingRole.get();
            }

            String adminEmail = "admin@synexis.com";

            Optional<Employee> existingEmployeeEmail = employeeRepo.findByEmployeeEmail(adminEmail);
            if (existingEmployeeEmail.isEmpty()){

                EmployeeDto admin = new EmployeeDto();

                admin.setEmployeePrefix("Mr.");
                admin.setEmployeeFirstName("Synexis");
                admin.setEmployeeLastName("Admin");
                admin.setEmployeeNIC("ADMIN123456789");
                admin.setEmployeeEmail(adminEmail);

                admin.setRoleName(roleName);

                employeeService.createEmployee(admin);
            }

            String role1Name = "ESTIMATOR";
            Optional<Role> existingEstimatorRole = roleRepo.findByRoleName(role1Name);
            if (existingEstimatorRole.isEmpty()) {
                adminRole = new Role();
                adminRole.setRoleName(role1Name);
                adminRole.setPrivileges(new HashSet<>());
                roleRepo.saveAndFlush(adminRole);
            }

            String role2Name = "SALESPERSON";
            Optional<Role> existingSalesPersonRole = roleRepo.findByRoleName(role2Name);
            if (existingSalesPersonRole.isEmpty()) {
                adminRole = new Role();
                adminRole.setRoleName(role2Name);
                adminRole.setPrivileges(new HashSet<>());
                roleRepo.saveAndFlush(adminRole);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Data initialization failed", e);
        }           
    }

}
