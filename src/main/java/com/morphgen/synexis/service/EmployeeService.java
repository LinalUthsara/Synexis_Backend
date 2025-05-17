package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.dto.EmployeeTableViewDto;
import com.morphgen.synexis.entity.Employee;

@Service

public interface EmployeeService {
    
    Employee createEmployee(EmployeeDto employeeDto);

    ResponseEntity<byte[]> viewEmployeeImage(Long employeeId);
    List<EmployeeTableViewDto> viewEmployeeTable();
}
