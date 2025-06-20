package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.EmployeeDropDownDto;
import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.dto.EmployeeLoginDto;
import com.morphgen.synexis.dto.EmployeeSideDropViewDto;
import com.morphgen.synexis.dto.EmployeeTableViewDto;
import com.morphgen.synexis.dto.EmployeeUpdateDto;
import com.morphgen.synexis.dto.EmployeeViewDto;
import com.morphgen.synexis.entity.Address;
import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.entity.EmployeeImage;
import com.morphgen.synexis.entity.Role;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.EmployeeNotFoundException;
import com.morphgen.synexis.exception.ImageProcessingException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.exception.RoleNotFoundException;
import com.morphgen.synexis.repository.EmployeeRepo;
import com.morphgen.synexis.repository.RoleRepo;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    @Transactional
    public Employee createEmployee(EmployeeDto employeeDto) {

        String defaultPassword = "Employee@123.";

        if(employeeDto.getEmployeeEmail() == null || employeeDto.getEmployeeEmail().isEmpty()){

            throw new InvalidInputException("Employee email cannot be empty!");
        }
        else if(employeeDto.getEmployeeNIC() == null || employeeDto.getEmployeeNIC().isEmpty()){

            throw new InvalidInputException("Employee NIC cannot be empty!");
        }
        else if(employeeDto.getRoleId() == null){

            throw new InvalidInputException("Employee Role cannot be empty!");
        }
        
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
                
                EmployeeImage employeeImage = new EmployeeImage();
                
                employeeImage.setEmployeeImageName(employeeDto.getEmployeeImage().getOriginalFilename());
                employeeImage.setEmployeeImageType(employeeDto.getEmployeeImage().getContentType());
                employeeImage.setEmployeeImageSize(employeeDto.getEmployeeImage().getSize());
                employeeImage.setEmployeeImageData(employeeDto.getEmployeeImage().getBytes());
                employeeImage.setEmployee(employee);

                employee.setEmployeeImage(employeeImage);
            }
        }
        catch (IOException e){

            throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
        }

        employee.setEmployeeEmail(employeeDto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(employeeDto.getEmployeePhoneNumber());

        Address address = new Address();

        address.setAddressLine1(employeeDto.getAddressLine1());
        address.setAddressLine2(employeeDto.getAddressLine2());
        address.setCity(employeeDto.getCity());
        address.setZipCode(employeeDto.getZipCode());

        employee.setEmployeeAddress(address);

        Optional<Role> role = roleRepo.findByRoleId(employeeDto.getRoleId());
        if (role.isEmpty()) {

            throw new RoleNotFoundException("Role ID not found: " + employeeDto.getRoleId());
        }

        employee.setRole(role.get());
        employee.setEmployeePassword(passwordEncoder.encode(defaultPassword));

        Employee newEmployee = employeeRepo.save(employee);

        activityLogService.logActivity(
            "Employee", 
            newEmployee.getEmployeeId(),
            newEmployee.getEmployeeFirstName(),
            Action.CREATE, 
            "Created Employee: " + newEmployee.getEmployeePrefix() + " " + newEmployee.getEmployeeFirstName() + " " + newEmployee.getEmployeeLastName());

        return newEmployee;
    }

    @Override
    public ResponseEntity<byte[]> viewEmployeeImage(Long employeeId) {
        
        Optional<Employee> optionalEmployee = employeeRepo.findById(employeeId);

        if (optionalEmployee.isPresent()) {

            Employee employee = optionalEmployee.get();
            EmployeeImage employeeImage = employee.getEmployeeImage();

            if (employeeImage != null && employeeImage.getEmployeeImageData() != null && employeeImage.getEmployeeImageData().length > 0) {
                
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .body(employeeImage.getEmployeeImageData());
            } else {

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
        } 
        else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public List<EmployeeTableViewDto> viewEmployeeTable() {
        
        List<Employee> employees = employeeRepo.findAllByOrderByEmployeeIdDesc();

        List<EmployeeTableViewDto> employeeTableViewDtoList = employees.stream().map(employee ->{

            EmployeeTableViewDto employeeTableViewDto = new EmployeeTableViewDto();

            employeeTableViewDto.setEmployeeId(employee.getEmployeeId());
            employeeTableViewDto.setEmployeeName(employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());
            employeeTableViewDto.setEmployeePhoneNumber(employee.getEmployeePhoneNumber());
            employeeTableViewDto.setEmployeeEmail(employee.getEmployeeEmail());
            employeeTableViewDto.setEmployeeStatus(employee.getEmployeeStatus());
            employeeTableViewDto.setRoleName(employee.getRole().getRoleName());

            if (employee.getEmployeeImage() != null && employee.getEmployeeImage().getEmployeeImageData() != null) {

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
            employeeSideDropViewDto.setEmployeeName(employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());

            if (employee.getEmployeeImage() != null && employee.getEmployeeImage().getEmployeeImageData() != null) {

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

        if (employee.getEmployeeAddress() != null){

        employeeViewDto.setAddressLine1(employee.getEmployeeAddress().getAddressLine1());
        employeeViewDto.setAddressLine2(employee.getEmployeeAddress().getAddressLine2());
        employeeViewDto.setCity(employee.getEmployeeAddress().getCity());
        employeeViewDto.setZipCode(employee.getEmployeeAddress().getZipCode());
        }
        
        employeeViewDto.setRoleName(employee.getRole().getRoleName());
        employeeViewDto.setRoleId(employee.getRole().getRoleId());

        employeeViewDto.setEmployeeStatus(employee.getEmployeeStatus());

        if (employee.getEmployeeImage() != null && employee.getEmployeeImage().getEmployeeImageData() != null) {

                String imageUrl = ImageUrlUtil.constructEmployeeImageUrl(employee.getEmployeeId());
                employeeViewDto.setEmployeeImageUrl(imageUrl);
            }

        return employeeViewDto;
    }

    @Override
    public Employee updateEmployee(Long employeeId, EmployeeDto employeeDto) {
        
        Employee employee = employeeRepo.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + employeeId + " is not found!"));

        if(employeeDto.getEmployeeEmail() == null || employeeDto.getEmployeeEmail().isEmpty()){

            throw new InvalidInputException("Employee email cannot be empty!");
        }
        else if(employeeDto.getEmployeeNIC() == null || employeeDto.getEmployeeNIC().isEmpty()){

            throw new InvalidInputException("Employee NIC cannot be empty!");
        }
        else if(employeeDto.getRoleId() == null){

            throw new InvalidInputException("Employee Role cannot be empty!");
        }

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

        EmployeeUpdateDto existingEmployee = EmployeeUpdateDto.builder()
        .employeeId(employee.getEmployeeId())
        .employeePrefix(employee.getEmployeePrefix())
        .employeeFirstName(employee.getEmployeeFirstName())
        .employeeLastName(employee.getEmployeeLastName())
        .employeeNIC(employee.getEmployeeNIC())
        .employeeDOB(employee.getEmployeeDOB())
        .employeeGender(employee.getEmployeeGender())
        .employeeEmail(employee.getEmployeeEmail())
        .employeePhoneNumber(employee.getEmployeePhoneNumber())
        .roleName(employee.getRole().getRoleName())
        .employeeImage(employee.getEmployeeImage() != null && employee.getEmployeeImage().getEmployeeImageData() !=null ? employee.getEmployeeImage().getEmployeeImageData().clone() : null)
        .addressLine1(employee.getEmployeeAddress() != null && employee.getEmployeeAddress().getAddressLine1() != null ? employee.getEmployeeAddress().getAddressLine1() : null)
        .addressLine2(employee.getEmployeeAddress() != null &&  employee.getEmployeeAddress().getAddressLine2() != null ? employee.getEmployeeAddress().getAddressLine2(): null)
        .city(employee.getEmployeeAddress() != null && employee.getEmployeeAddress().getCity() != null ? employee.getEmployeeAddress().getCity() : null)
        .zipCode(employee.getEmployeeAddress() != null && employee.getEmployeeAddress().getZipCode() != null ? employee.getEmployeeAddress().getZipCode() : null)
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
                
                EmployeeImage employeeImage = employee.getEmployeeImage();

                if (employeeImage == null){
                    
                    employeeImage = new EmployeeImage();
                }

                employeeImage.setEmployeeImageName(employeeDto.getEmployeeImage().getOriginalFilename());
                employeeImage.setEmployeeImageType(employeeDto.getEmployeeImage().getContentType());
                employeeImage.setEmployeeImageSize(employeeDto.getEmployeeImage().getSize());
                employeeImage.setEmployeeImageData(employeeDto.getEmployeeImage().getBytes());
                employeeImage.setEmployee(employee);

                employee.setEmployeeImage(employeeImage);
            }
            else if (employee.getEmployeeImage() != null){
                
                EmployeeImage employeeImage = employee.getEmployeeImage();

                employeeImage.setEmployeeImageName(null);
                employeeImage.setEmployeeImageType(null);
                employeeImage.setEmployeeImageSize(null);
                employeeImage.setEmployeeImageData(null);
                employeeImage.setEmployee(employee);

                employee.setEmployeeImage(null);
            }
        }
        catch(IOException e){
            throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
        }

        employee.setEmployeeEmail(employeeDto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(employeeDto.getEmployeePhoneNumber());

        Address address = new Address();

        address.setAddressLine1(employeeDto.getAddressLine1());
        address.setAddressLine2(employeeDto.getAddressLine2());
        address.setCity(employeeDto.getCity());
        address.setZipCode(employeeDto.getZipCode());

        employee.setEmployeeAddress(address);

        Optional<Role> role = roleRepo.findByRoleId(employeeDto.getRoleId());
        if (role.isEmpty()) {
            throw new RoleNotFoundException("Role ID not found: " + employeeDto.getRoleId());
        }

        employee.setRole(role.get());

        Employee updatedEmployee = employeeRepo.save(employee);

        EmployeeUpdateDto newEmployee = EmployeeUpdateDto.builder()
        .employeeId(updatedEmployee.getEmployeeId())
        .employeePrefix(updatedEmployee.getEmployeePrefix())
        .employeeFirstName(updatedEmployee.getEmployeeFirstName())
        .employeeLastName(updatedEmployee.getEmployeeLastName())
        .employeeNIC(updatedEmployee.getEmployeeNIC())
        .employeeDOB(updatedEmployee.getEmployeeDOB())
        .employeeGender(updatedEmployee.getEmployeeGender())
        .employeeEmail(updatedEmployee.getEmployeeEmail())
        .employeePhoneNumber(updatedEmployee.getEmployeePhoneNumber())
        .roleName(updatedEmployee.getRole().getRoleName())
        .employeeImage(updatedEmployee.getEmployeeImage() != null && updatedEmployee.getEmployeeImage().getEmployeeImageData() !=null ? updatedEmployee.getEmployeeImage().getEmployeeImageData().clone() : null)
        .addressLine1(updatedEmployee.getEmployeeAddress() != null && updatedEmployee.getEmployeeAddress().getAddressLine1() != null ? updatedEmployee.getEmployeeAddress().getAddressLine1() : null)
        .addressLine2(updatedEmployee.getEmployeeAddress() != null &&  updatedEmployee.getEmployeeAddress().getAddressLine2() != null ? updatedEmployee.getEmployeeAddress().getAddressLine2(): null)
        .city(updatedEmployee.getEmployeeAddress() != null && updatedEmployee.getEmployeeAddress().getCity() != null ? updatedEmployee.getEmployeeAddress().getCity() : null)
        .zipCode(updatedEmployee.getEmployeeAddress() != null && updatedEmployee.getEmployeeAddress().getZipCode() != null ? updatedEmployee.getEmployeeAddress().getZipCode() : null)
        .employeeStatus(updatedEmployee.getEmployeeStatus())
        .build();

        String changes = EntityDiffUtil.describeChanges(existingEmployee, newEmployee);

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
            "Deleted Employee: " + employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());
    }

    @Override
    public void reactivateEmployee(Long employeeId) {
        
        Employee employee = employeeRepo.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + employeeId + " is not found!"));

        if (employee.getEmployeeStatus() == Status.ACTIVE){
            throw new DataIntegrityViolationException("Employee is already active!");
        }

        employee.setEmployeeStatus(Status.ACTIVE);

        employeeRepo.save(employee);

        activityLogService.logActivity(
            "Employee", 
            employee.getEmployeeId(),
            employee.getEmployeeFirstName(), 
            Action.REACTIVATE, 
            "Reactivated Employee: " + employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());
    }

    @Override
    public List<EmployeeDropDownDto> employeeDropDown(String roleName, String searchEmployee) {

        Optional<Role> role = roleRepo.findByRoleName(roleName);
        if (role.isEmpty()){
            throw new DataIntegrityViolationException("Role: " + roleName + " does not exists!");
        }
        Role exisitingRole = role.get();

        List<Employee> employees = employeeRepo.searchActiveEmployees(exisitingRole.getRoleId(), searchEmployee);

        List<EmployeeDropDownDto> employeeDropDownDtoList = employees.stream().map(employee ->{

            EmployeeDropDownDto employeeDropDownDto = new EmployeeDropDownDto();

            employeeDropDownDto.setEmployeeId(employee.getEmployeeId());
            employeeDropDownDto.setEmployeeName(employee.getEmployeePrefix() + " " + employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());

            return employeeDropDownDto;
        }).collect(Collectors.toList());

        return employeeDropDownDtoList;
    }

    @Override
    public EmployeeLoginDto viewEmployeeByEmail(String employeeEmail) {
        
        Optional<Employee> ExistingEmployee = employeeRepo.findByEmployeeEmail(employeeEmail);
        if (ExistingEmployee.isEmpty()){
            throw new DataIntegrityViolationException("Employee Email: " + employeeEmail + " is not found!");
        }

        Employee employee = ExistingEmployee.get();

        EmployeeLoginDto employeeLoginDto = new EmployeeLoginDto();

        employeeLoginDto.setEmployeeId(employee.getEmployeeId());
        employeeLoginDto.setEmployeeName(employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());
        employeeLoginDto.setRoleName(employee.getRole().getRoleName());
        employeeLoginDto.setIsPasswordChanged(employee.getIsPasswordChanged());

        return employeeLoginDto;
    }

}