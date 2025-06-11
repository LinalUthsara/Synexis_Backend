package com.morphgen.synexis.security.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.exception.EmployeeNotFoundException;
import com.morphgen.synexis.repository.EmployeeRepo;

@Service

public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Employee employee = employeeRepo.findByEmployeeEmail(username)
        .orElseThrow(() -> new EmployeeNotFoundException("Employee with email: " + username + " is not found!"));

        Set<GrantedAuthority> authorities = employee.getRole().getPrivileges().stream()
            .map(p -> new SimpleGrantedAuthority(p.getPrivilegeName()))
            .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
            employee.getEmployeeEmail(), employee.getEmployeePassword(), authorities);
    }
    
}
