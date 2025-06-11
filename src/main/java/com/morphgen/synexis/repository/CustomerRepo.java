package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Customer;

@Repository

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByCustomerEmail(String customerEmail);

    List<Customer> findAllByOrderByCustomerIdDesc();

    @Query("""
    SELECT c FROM Customer c 
    WHERE c.customerStatus = ACTIVE AND (
        LOWER(c.customerFirstName) LIKE LOWER(CONCAT(:searchCustomer, '%')) 
        OR LOWER(c.customerFirstName) LIKE LOWER(CONCAT('% ', :searchCustomer, '%'))
        OR LOWER(c.customerLastName) LIKE LOWER(CONCAT(:searchCustomer, '%'))
        OR LOWER(c.customerLastName) LIKE LOWER(CONCAT('% ', :searchCustomer, '%'))
        OR LOWER(c.customerEmail) LIKE LOWER(CONCAT(:searchCustomer, '%'))
        OR LOWER(c.customerEmail) LIKE LOWER(CONCAT('% ', :searchCustomer, '%'))
    )""")
    List<Customer> searchActiveCustomers(@Param("searchCustomer") String searchCustomer);
}
