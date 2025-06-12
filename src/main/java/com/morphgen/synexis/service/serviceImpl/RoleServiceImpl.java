package com.morphgen.synexis.service.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.RoleDto;
import com.morphgen.synexis.entity.Privilege;
import com.morphgen.synexis.entity.Role;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.NotificationType;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.repository.PrivilegeRepo;
import com.morphgen.synexis.repository.RoleRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.NotificationService;
import com.morphgen.synexis.service.RoleService;

@Service

public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PrivilegeRepo privilegeRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public Role createRole(RoleDto roleDto) {
        
        if(roleDto.getRoleName() == null || roleDto.getRoleName().isEmpty()){
            throw new InvalidInputException("Role name cannot be empty!");
        }
        else if(roleDto.getPrivilegeNames() == null || roleDto.getPrivilegeNames().isEmpty()){
            throw new InvalidInputException("A role must have at least one assigned privilege!");
        }

        Optional<Role> existingRole = roleRepo.findByRoleName(roleDto.getRoleName());
        if (existingRole.isPresent()){

            Role activeRole = existingRole.get();

            if (activeRole.getRoleStatus() == Status.ACTIVE){

                throw new DataIntegrityViolationException("A Role named " + roleDto.getRoleName() + " already exists!");
            }
            else{

                throw new DataIntegrityViolationException("A Role named " + roleDto.getRoleName() + " already exists but is currently inactive. Consider reactivating it.");
            }
        }

        List<Privilege> privilegeList = privilegeRepo.findByPrivilegeNameIn(roleDto.getPrivilegeNames());
        Set<Privilege> privileges = new HashSet<>(privilegeList);

        Role role = new Role();

        role.setRoleName(roleDto.getRoleName());
        role.setPrivileges(privileges);

        Role newRole = roleRepo.save(role);

        activityLogService.logActivity(
            "Role", 
            newRole.getRoleId(),
            newRole.getRoleName(),
            Action.CREATE, 
            "Created Role: " + newRole.getRoleName());

        notificationService.createNotification(
            "New Role Created", 
            newRole.getRoleName() + " has been created in the inventory.", 
            NotificationType.INFO, 
            "ROLE");

        return newRole;
    }
}
