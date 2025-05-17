package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.dto.EmployeeTableViewDto;
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

    @GetMapping("/image/{employeeId}")
    public ResponseEntity<byte[]> viewEmployeeImage(@PathVariable Long employeeId) {
        return employeeService.viewEmployeeImage(employeeId);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeTableViewDto>> viewEmployeeTable(){
        
        List<EmployeeTableViewDto> employeeTableViewDtoList = employeeService.viewEmployeeTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(employeeTableViewDtoList);
    }
}
