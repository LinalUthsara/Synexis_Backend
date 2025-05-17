package com.morphgen.synexis.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.service.EmployeeService;

@RestController
@RequestMapping("api/synexis/employee")
@CrossOrigin("*")

public class EmployeeController {
    
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<String> createEmployee(@ModelAttribute EmployeeDto employeeDto) throws IOException {
        
        if(employeeDto.getEmployeeEmail() == null || employeeDto.getEmployeeEmail().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Employee Email is Required!");
        }
        else if(employeeDto.getEmployeeNIC() == null || employeeDto.getEmployeeNIC().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Employee NIC is Required!");
        }

        employeeService.createEmployee(employeeDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Employee successfully created!");
    }
}
