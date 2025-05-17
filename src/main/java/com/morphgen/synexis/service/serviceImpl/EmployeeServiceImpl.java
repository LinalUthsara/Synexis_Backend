package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.entity.Address;
import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.repository.EmployeeRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.EmployeeService;

@Service

public class EmployeeServiceImpl implements EmployeeService {
    
    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public Employee createEmployee(EmployeeDto employeeDto) {
        
        Optional<Employee> existingEmployeeEmail = employeeRepo.findByEmployeeEmail(employeeDto.getEmployeeEmail());
        if (existingEmployeeEmail.isPresent()){
            throw new DataIntegrityViolationException("An Employee with the email " + employeeDto.getEmployeeEmail() + " already exists!");
        }

        Optional<Employee> existingEmployeeNIC = employeeRepo.findByEmployeeNIC(employeeDto.getEmployeeNIC());
        if (existingEmployeeNIC.isPresent()){
            throw new DataIntegrityViolationException("An Employee with the NIC " + employeeDto.getEmployeeNIC() + " already exists!");
        }

        Employee employee = new Employee();

        employee.setEmployeePrefix(employeeDto.getEmployeePrefix());
        employee.setEmployeeFirstName(employeeDto.getEmployeeFirstName());
        employee.setEmployeeLastName(employeeDto.getEmployeeLastName());
        employee.setEmployeeNIC(employeeDto.getEmployeeNIC());
        employee.setEmployeeDOB(employeeDto.getEmployeeDOB());

        try{
            if (employeeDto.getEmployeeImage() != null && !employeeDto.getEmployeeImage().isEmpty()) {
                employee.setEmployeeImage(employeeDto.getEmployeeImage().getBytes());
            }
        }
        catch(IOException e){
            throw new IllegalArgumentException("Failed to process image file!");
        }

        employee.setEmployeeEmail(employeeDto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(employeeDto.getEmployeePhoneNumber());

        Address address = new Address();

        address.setAddressLine1(employeeDto.getAddressLine1());
        address.setAddressLine2(employeeDto.getAddressLine2());
        address.setCity(employeeDto.getCity());
        address.setZipCode(employeeDto.getZipCode());

        employee.setEmployeeAddress(address);

        employee.setRole(employeeDto.getRole());

        Employee newEmployee = employeeRepo.save(employee);

        activityLogService.logActivity(
            "Employee", 
            newEmployee.getEmployeeId(),
            newEmployee.getEmployeeFirstName(),
            Action.CREATE, 
            "Created Employee: " + newEmployee.getEmployeeFirstName());

        return newEmployee;
    }
}