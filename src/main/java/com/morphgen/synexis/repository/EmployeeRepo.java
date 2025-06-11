package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Employee;

@Repository

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeEmail(String employeeEmail);
    Optional<Employee> findByEmployeeNIC(String employeeNIC);

    List<Employee> findAllByOrderByEmployeeIdDesc();

    @Query("""
    SELECT e FROM Employee e 
    WHERE e.employeeStatus = ACTIVE 
    AND e.role.roleId = :roleId
    AND (
        LOWER(e.employeeFirstName) LIKE LOWER(CONCAT(:searchEmployee, '%')) 
        OR LOWER(e.employeeFirstName) LIKE LOWER(CONCAT('% ', :searchEmployee, '%'))
        OR LOWER(e.employeeLastName) LIKE LOWER(CONCAT(:searchEmployee, '%'))
        OR LOWER(e.employeeLastName) LIKE LOWER(CONCAT('% ', :searchEmployee, '%'))
        OR LOWER(e.employeeNIC) LIKE LOWER(CONCAT(:searchEmployee, '%'))
        OR LOWER(e.employeeNIC) LIKE LOWER(CONCAT('% ', :searchEmployee, '%'))
        OR LOWER(e.employeeEmail) LIKE LOWER(CONCAT(:searchEmployee, '%'))
        OR LOWER(e.employeeEmail) LIKE LOWER(CONCAT('% ', :searchEmployee, '%'))
        OR LOWER(e.employeePhoneNumber) LIKE LOWER(CONCAT(:searchEmployee, '%'))
        OR LOWER(e.employeePhoneNumber) LIKE LOWER(CONCAT('% ', :searchEmployee, '%'))
    )""")
    List<Employee> searchActiveEmployees(@Param("roleId") Long roleId, @Param("searchEmployee") String searchEmployee);
}
