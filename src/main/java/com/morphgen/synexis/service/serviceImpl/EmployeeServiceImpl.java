package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.dto.EmployeeSideDropViewDto;
import com.morphgen.synexis.dto.EmployeeTableViewDto;
import com.morphgen.synexis.dto.EmployeeViewDto;
import com.morphgen.synexis.entity.Address;
import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.EmployeeNotFoundException;
import com.morphgen.synexis.repository.EmployeeRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.EmployeeService;
import com.morphgen.synexis.utils.EntityDiffUtil;
import com.morphgen.synexis.utils.ImageUrlUtil;

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
        employee.setEmployeeGender(employeeDto.getEmployeeGender());

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

    @Override
    public ResponseEntity<byte[]> viewEmployeeImage(Long employeeId) {
        
        return employeeRepo.findById(employeeId)
            .filter(employee -> employee.getEmployeeImage() != null)
            .map(employee -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(employee.getEmployeeImage()))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public List<EmployeeTableViewDto> viewEmployeeTable() {
        
        List<Employee> employees = employeeRepo.findAllByOrderByEmployeeIdDesc();

        List<EmployeeTableViewDto> employeeTableViewDtoList = employees.stream().map(employee ->{

            EmployeeTableViewDto employeeTableViewDto = new EmployeeTableViewDto();

            employeeTableViewDto.setEmployeeId(employee.getEmployeeId());
            employeeTableViewDto.setEmployeeName(employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName());
            employeeTableViewDto.setEmployeePhoneNumber(employee.getEmployeePhoneNumber());
            employeeTableViewDto.setEmployeeEmail(employee.getEmployeeEmail());
            employeeTableViewDto.setEmployeeStatus(employee.getEmployeeStatus());
            employeeTableViewDto.setRole(employee.getRole());

            if (employee.getEmployeeImage() != null) {
                String imageUrl = ImageUrlUtil.constructEmployeeImageUrl(employee.getEmployeeId());
                employeeTableViewDto.setEmployeeImageUrl(imageUrl);
            }

            return employeeTableViewDto;
        }).collect(Collectors.toList());

        return employeeTableViewDtoList;
    }

    @Override
    public List<EmployeeSideDropViewDto> viewEmployeeSideDrop() {
        
        List<Employee> employees = employeeRepo.findAllByOrderByEmployeeIdDesc();

        List<EmployeeSideDropViewDto> employeeSideDropViewDtoList = employees.stream().map(employee ->{

            EmployeeSideDropViewDto employeeSideDropViewDto = new EmployeeSideDropViewDto();

            employeeSideDropViewDto.setEmployeeId(employee.getEmployeeId());
            employeeSideDropViewDto.setEmployeeName(employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName());

            if (employee.getEmployeeImage() != null) {
                String imageUrl = ImageUrlUtil.constructEmployeeImageUrl(employee.getEmployeeId());
                employeeSideDropViewDto.setEmployeeImageUrl(imageUrl);
            }

            return employeeSideDropViewDto;
        }).collect(Collectors.toList());

        return employeeSideDropViewDtoList;
    }

    @Override
    public EmployeeViewDto viewEmployeeById(Long employeeId) {
        
        Employee employee = employeeRepo.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + employeeId + " is not found!"));

        EmployeeViewDto employeeViewDto = new EmployeeViewDto();

        employeeViewDto.setEmployeeId(employee.getEmployeeId());
        employeeViewDto.setEmployeePrefix(employee.getEmployeePrefix());
        employeeViewDto.setEmployeeFirstName(employee.getEmployeeFirstName());
        employeeViewDto.setEmployeeLastName(employee.getEmployeeLastName());
        employeeViewDto.setEmployeeNIC(employee.getEmployeeNIC());
        employeeViewDto.setEmployeeDOB(employee.getEmployeeDOB());
        employeeViewDto.setEmployeeGender(employee.getEmployeeGender());
        employeeViewDto.setEmployeeEmail(employee.getEmployeeEmail());
        employeeViewDto.setEmployeePhoneNumber(employee.getEmployeePhoneNumber());
        employeeViewDto.setAddressLine1(employee.getEmployeeAddress().getAddressLine1());
        employeeViewDto.setAddressLine2(employee.getEmployeeAddress().getAddressLine2());
        employeeViewDto.setCity(employee.getEmployeeAddress().getCity());
        employeeViewDto.setZipCode(employee.getEmployeeAddress().getZipCode());
        employeeViewDto.setRole(employee.getRole());

        if (employee.getEmployeeImage() != null) {
                String imageUrl = ImageUrlUtil.constructEmployeeImageUrl(employee.getEmployeeId());
                employeeViewDto.setEmployeeImageUrl(imageUrl);
            }

        return employeeViewDto;
    }

    @Override
    public Employee updateEmployee(Long employeeId, EmployeeDto employeeDto) {
        
        Employee employee = employeeRepo.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + employeeId + " is not found!"));

        if (!employee.getEmployeeEmail().equalsIgnoreCase(employeeDto.getEmployeeEmail())) {
            Optional<Employee> existingEmployeeEmail = employeeRepo.findByEmployeeEmail(employeeDto.getEmployeeEmail());
            if (existingEmployeeEmail.isPresent()){
                throw new DataIntegrityViolationException("An Employee with the email " + employeeDto.getEmployeeEmail() + " already exists!");
            }
        }
        if (!employee.getEmployeeNIC().equalsIgnoreCase(employeeDto.getEmployeeNIC())) {
            Optional<Employee> existingEmployeeNIC = employeeRepo.findByEmployeeNIC(employeeDto.getEmployeeNIC());
            if (existingEmployeeNIC.isPresent()){
                throw new DataIntegrityViolationException("An Employee with the NIC " + employeeDto.getEmployeeNIC() + " already exists!");
            }
        }

        Address existingAddress = new Address();
        existingAddress.setAddressLine1(employee.getEmployeeAddress().getAddressLine1());
        existingAddress.setAddressLine2(employee.getEmployeeAddress().getAddressLine2());
        existingAddress.setCity(employee.getEmployeeAddress().getCity());
        existingAddress.setZipCode(employee.getEmployeeAddress().getZipCode());

        Employee existingEmployee = Employee.builder()
        .employeeId(employee.getEmployeeId())
        .employeePrefix(employee.getEmployeePrefix())
        .employeeFirstName(employee.getEmployeeFirstName())
        .employeeLastName(employee.getEmployeeLastName())
        .employeeNIC(employee.getEmployeeNIC())
        .employeeDOB(employee.getEmployeeDOB())
        .employeeGender(employee.getEmployeeGender())
        .employeeEmail(employee.getEmployeeEmail())
        .employeePhoneNumber(employee.getEmployeePhoneNumber())
        .Role(employee.getRole())
        .employeeImage(employee.getEmployeeImage() != null ? employee.getEmployeeImage().clone() : null)
        .employeeAddress(existingAddress)
        .employeeStatus(employee.getEmployeeStatus())
        .build();


        employee.setEmployeePrefix(employeeDto.getEmployeePrefix());
        employee.setEmployeeFirstName(employeeDto.getEmployeeFirstName());
        employee.setEmployeeLastName(employeeDto.getEmployeeLastName());
        employee.setEmployeeNIC(employeeDto.getEmployeeNIC());
        employee.setEmployeeDOB(employeeDto.getEmployeeDOB());
        employee.setEmployeeGender(employeeDto.getEmployeeGender());

        try{
            if (employeeDto.getEmployeeImage() != null && !employeeDto.getEmployeeImage().isEmpty()) {
                employee.setEmployeeImage(employeeDto.getEmployeeImage().getBytes());
            }
            else if (employeeDto.getEmployeeImage() == null || employeeDto.getEmployeeImage().isEmpty()){
                employee.setEmployeeImage(null);
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

        Employee updatedEmployee = employeeRepo.save(employee);

        String changes = EntityDiffUtil.describeChanges(existingEmployee, updatedEmployee);

        activityLogService.logActivity(
            "Employee", 
            updatedEmployee.getEmployeeId(),
            updatedEmployee.getEmployeeFirstName(), 
            Action.UPDATE, 
            changes.isBlank() ? "No changes detected" : changes);

        return updatedEmployee;
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        
        Employee employee = employeeRepo.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + employeeId + " is not found!"));

        employee.setEmployeeStatus(Status.INACTIVE);

        employeeRepo.save(employee);

        activityLogService.logActivity(
            "Employee", 
            employee.getEmployeeId(),
            employee.getEmployeeFirstName(), 
            Action.DELETE, 
            "Deleted Employee: " + employee.getEmployeeFirstName());
    }

}