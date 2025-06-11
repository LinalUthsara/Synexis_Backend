package com.morphgen.synexis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Privilege;

@Repository

public interface PrivilegeRepo extends JpaRepository<Privilege, Long> {
    
    Optional<Privilege> findByPrivilegeName(String privilegeName);
    
}
