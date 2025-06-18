package com.morphgen.synexis.dto;

import java.util.List;

import lombok.Data;

@Data

public class RoleDto {
    
    private String roleName;
    private List<String> privilegeNames;
    
}
