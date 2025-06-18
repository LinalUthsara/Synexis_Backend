package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.RoleDropDownDto;
import com.morphgen.synexis.dto.RoleDto;
import com.morphgen.synexis.service.RoleService;

@RestController
@RequestMapping("api/synexis/role")
@CrossOrigin("*")

public class RoleController {
    
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public ResponseEntity<String> createRole(@RequestBody RoleDto roleDto) throws IOException {

        roleService.createRole(roleDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Role successfully created!");
    }

    @GetMapping("/search")
    // @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    public ResponseEntity<List<RoleDropDownDto>> roleDropDown(@RequestParam String searchRole){

        List<RoleDropDownDto> roleDropDownDtoList = roleService.roleDropDown(searchRole);

        return ResponseEntity.status(HttpStatus.OK).body(roleDropDownDtoList);

    }

}
