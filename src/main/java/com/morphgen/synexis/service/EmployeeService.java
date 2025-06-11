package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.EmployeeDropDownDto;
import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.dto.EmployeeLoginDto;
import com.morphgen.synexis.dto.EmployeeSideDropViewDto;
import com.morphgen.synexis.dto.EmployeeTableViewDto;
import com.morphgen.synexis.dto.EmployeeViewDto;
import com.morphgen.synexis.entity.Employee;

@Service

public interface EmployeeService {
    
    Employee createEmployee(EmployeeDto employeeDto);

    ResponseEntity<byte[]> viewEmployeeImage(Long employeeId);
    List<EmployeeTableViewDto> viewEmployeeTable();
    List<EmployeeSideDropViewDto> viewEmployeeSideDrop();
    EmployeeViewDto viewEmployeeById(Long employeeId);

    Employee updateEmployee(Long employeeId, EmployeeDto employeeDto);

    void deleteEmployee(Long employeeId);

    void reactivateEmployee(Long employeeId);

    List<EmployeeDropDownDto> employeeDropDown(String roleName, String searchEmployee);

    EmployeeLoginDto viewEmployeeByEmail(String employeeEmail);
}
