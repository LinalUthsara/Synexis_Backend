package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.ActivityLogSideDropViewDto;
import com.morphgen.synexis.dto.ActivityLogTableViewDto;
import com.morphgen.synexis.dto.ActivityLogViewDto;
import com.morphgen.synexis.enums.Action;

@Service

public interface ActivityLogService {
    
    void logActivity(String entity, Long entityId, String entityName, Action actLogAction, String actLogDetails);

    List<ActivityLogSideDropViewDto> viewActivityByEntity(String entity);
    List<ActivityLogViewDto> viewActivityByEntityAndId(String entity, Long entityId);
    List<ActivityLogTableViewDto> viewAllActivityLog();
}
