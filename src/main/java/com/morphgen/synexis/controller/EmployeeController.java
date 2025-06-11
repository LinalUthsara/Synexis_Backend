package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.EmployeeDropDownDto;
import com.morphgen.synexis.dto.EmployeeDto;
import com.morphgen.synexis.dto.EmployeeLoginDto;
import com.morphgen.synexis.dto.EmployeeSideDropViewDto;
import com.morphgen.synexis.dto.EmployeeTableViewDto;
import com.morphgen.synexis.dto.EmployeeViewDto;
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
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    public ResponseEntity<String> createEmployee(@ModelAttribute EmployeeDto employeeDto) throws IOException {
        
        employeeService.createEmployee(employeeDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Employee successfully created!");
    }

    @GetMapping("/image/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    public ResponseEntity<byte[]> viewEmployeeImage(@PathVariable Long employeeId) {
        return employeeService.viewEmployeeImage(employeeId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    public ResponseEntity<List<EmployeeTableViewDto>> viewEmployeeTable(){
        
        List<EmployeeTableViewDto> employeeTableViewDtoList = employeeService.viewEmployeeTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(employeeTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    public ResponseEntity<List<EmployeeSideDropViewDto>> viewEmployeeSideDrop(){

        List<EmployeeSideDropViewDto> employeeSideDropViewDtoList = employeeService.viewEmployeeSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(employeeSideDropViewDtoList);

    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    public ResponseEntity<EmployeeViewDto> viewEmployeeById(@PathVariable Long employeeId){

        EmployeeViewDto employeeViewDto = employeeService.viewEmployeeById(employeeId);

        return ResponseEntity.status(HttpStatus.OK).body(employeeViewDto);
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    public ResponseEntity<String> updateEmployee(@PathVariable Long employeeId, @ModelAttribute EmployeeDto employeeDto){
        
        employeeService.updateEmployee(employeeId, employeeDto);

        return ResponseEntity.status(HttpStatus.OK).body("Employee successfully updated!");
    }

    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId){

        employeeService.deleteEmployee(employeeId);

        return ResponseEntity.status(HttpStatus.OK).body("Employee successfully deleted!");
    }

    @PatchMapping("/reactivate/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_REACTIVATE')")
    public ResponseEntity<String> reactivateEmployee(@PathVariable Long employeeId){

        employeeService.reactivateEmployee(employeeId);

        return ResponseEntity.status(HttpStatus.OK).body("Employee successfully reactivated!");
    }

    @GetMapping("/search/{roleName}")
    @PreAuthorize("hasAuthority('INQUIRY_CREATE')")
    public ResponseEntity<List<EmployeeDropDownDto>> employeeDropDown(@PathVariable String roleName, @RequestParam String searchEmployee){

        List<EmployeeDropDownDto> employeeDropDownDtoList = employeeService.employeeDropDown(roleName, searchEmployee);

        return ResponseEntity.status(HttpStatus.OK).body(employeeDropDownDtoList);

    }

    @GetMapping("/email")
    public ResponseEntity<EmployeeLoginDto> viewEmployeeByEmail(@RequestParam String employeeEmail){

        EmployeeLoginDto employeeLoginDto = employeeService.viewEmployeeByEmail(employeeEmail);

        return ResponseEntity.status(HttpStatus.OK).body(employeeLoginDto);
    }

}
