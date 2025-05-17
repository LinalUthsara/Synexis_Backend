package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.entity.Employee;

@Service

public interface EmployeeService {
    
    Employee createEmployee(EmployeeDto employeeDto);
    
}
