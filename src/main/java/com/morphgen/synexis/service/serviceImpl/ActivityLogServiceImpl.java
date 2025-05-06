package com.morphgen.synexis.service.serviceImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.ActivityLogSideDropViewDto;
import com.morphgen.synexis.dto.ActivityLogTableViewDto;
import com.morphgen.synexis.dto.ActivityLogViewDto;
import com.morphgen.synexis.entity.ActivityLog;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.repository.ActivityLogRepo;
import com.morphgen.synexis.service.ActivityLogService;

@Service 

public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepo activityLogRepo;

    @Override
    public void logActivity(String entity, Long entityId, String entityName, Action actLogAction, String actLogDetails) {
        
        ActivityLog newLog = ActivityLog.builder()
                .entity(entity)
                .entityId(entityId)
                .entityName(entityName)
                .actLogAction(actLogAction)
                .actLogDetails(actLogDetails)
                .build();
        activityLogRepo.save(newLog);
    }


    @Override
    public List<ActivityLogSideDropViewDto> viewActivityByEntity(String entity) {
        
        List<ActivityLog> activityLogs = activityLogRepo.findByEntityOrderByActLogTimestampDesc(entity);

        List<ActivityLogSideDropViewDto> activityLogSideDropViewDtoList = activityLogs.stream().map(activitylog ->{

            ActivityLogSideDropViewDto activityLogSideDropViewDto = new ActivityLogSideDropViewDto();

            activityLogSideDropViewDto.setEntityName(activitylog.getEntityName());

            activityLogSideDropViewDto.setActLogAction(switch(activitylog.getActLogAction()){
                case CREATE -> "created By";
                case UPDATE -> "updated By";
                case DELETE -> "deleted By";
            });

            activityLogSideDropViewDto.setActLogTimestamp(activitylog.getActLogTimestamp()
            .format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm")));

            return activityLogSideDropViewDto;
        }).collect(Collectors.toList());

        return activityLogSideDropViewDtoList;
    }


    @Override
    public List<ActivityLogViewDto> viewActivityByEntityAndId(String entity, Long entityId) {
        
        List<ActivityLog> activityLogs = activityLogRepo.findByEntityAndEntityIdOrderByActLogTimestampDesc(entity, entityId);

        List<ActivityLogViewDto> activityLogViewDtoList = activityLogs.stream().map(activitylog ->{

            ActivityLogViewDto activityLogViewDto = new ActivityLogViewDto();

            activityLogViewDto.setActLogTimestamp(activitylog.getActLogTimestamp()
            .format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm")));

            activityLogViewDto.setActLogAction(switch(activitylog.getActLogAction()){
                case CREATE -> "created By";
                case UPDATE -> "updated By";
                case DELETE -> "deleted By";
            });

            activityLogViewDto.setActLogDetails(activitylog.getActLogDetails());

            return activityLogViewDto;
        }).collect(Collectors.toList());

        return activityLogViewDtoList;
    }


    @Override
    public List<ActivityLogTableViewDto> viewAllActivityLog() {
        
        List<ActivityLog> activityLogs = activityLogRepo.findAllByOrderByActLogTimestampDesc();

        List<ActivityLogTableViewDto> activityLogTableViewDtoList = activityLogs.stream().map(activitylog ->{

            ActivityLogTableViewDto activityLogTableViewDto = new ActivityLogTableViewDto();

            activityLogTableViewDto.setActLogId(activitylog.getActLogId());

            activityLogTableViewDto.setActLogTimestamp(activitylog.getActLogTimestamp()
            .format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm")));

            activityLogTableViewDto.setEntity(activitylog.getEntity());

            activityLogTableViewDto.setEntityName(activitylog.getEntityName());

            activityLogTableViewDto.setActLogAction(activitylog.getActLogAction());

            activityLogTableViewDto.setActLogDetails(activitylog.getActLogDetails());

            return activityLogTableViewDto;
        }).collect(Collectors.toList());

        return activityLogTableViewDtoList;
    }
    
}

