package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.RoleDropDownDto;
import com.morphgen.synexis.dto.RoleDto;
import com.morphgen.synexis.entity.Role;

@Service

public interface RoleService {
    
    Role createRole(RoleDto roleDto);

    List<RoleDropDownDto> roleDropDown(String searchRole);


}