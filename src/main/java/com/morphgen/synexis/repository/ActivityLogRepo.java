package com.morphgen.synexis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.ActivityLog;

@Repository

public interface ActivityLogRepo extends JpaRepository<ActivityLog, Long> {

    // Retrieves all activity logs with the most recent log first
    List<ActivityLog> findAllByOrderByActLogTimestampDesc();
    
    //Retrieves all activity logs for a specific entity type, with the most recent log first
    List<ActivityLog> findByEntityOrderByActLogTimestampDesc(String entity);

    //Retrieves all activity logs for a specific entity and entity ID, with the most recent log first
    List<ActivityLog> findByEntityAndEntityIdOrderByActLogTimestampDesc(String entity, Long entityId);

    
}
