package com.morphgen.synexis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Employee;

@Repository

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeEmail(String employeeEmail);
    Optional<Employee> findByEmployeeNIC(String employeeNIC);
}
