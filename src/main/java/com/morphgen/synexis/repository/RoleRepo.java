package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Role;

@Repository

public interface RoleRepo extends JpaRepository<Role, Long> {
    
    Optional<Role> findByRoleName(String roleName);

    Optional<Role> findByRoleId(Long roleId);

    @Query("""
    SELECT r FROM Role r 
    WHERE r.roleStatus = ACTIVE AND (
        LOWER(r.roleName) LIKE LOWER(CONCAT(:searchRole, '%')) 
        OR LOWER(r.roleName) LIKE LOWER(CONCAT('% ', :searchRole, '%'))
    )""")
    List<Role> searchActiveRoles(@Param("searchRole") String searchRole);

}

