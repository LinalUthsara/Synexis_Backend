package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.RoleDto;
import com.morphgen.synexis.entity.Role;

@Service

public interface RoleService {
    
    Role createRole(RoleDto roleDto);
}
