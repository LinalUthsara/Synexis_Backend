package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Customer;

@Repository

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByCustomerEmail(String customerEmail);

    List<Customer> findAllByOrderByCustomerIdDesc();
}
