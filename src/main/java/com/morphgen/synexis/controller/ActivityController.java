package com.morphgen.synexis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.ActivityLogSideDropViewDto;
import com.morphgen.synexis.dto.ActivityLogTableViewDto;
import com.morphgen.synexis.dto.ActivityLogViewDto;
import com.morphgen.synexis.service.ActivityLogService;

@RestController
@RequestMapping("api/synexis/activityLog")
@CrossOrigin("*")

public class ActivityController {
    
    @Autowired
    private ActivityLogService activityLogService;

    //Activity Log of a specific entity(eg: Brand)
    @GetMapping("/{entity}")
    @PreAuthorize("hasAuthority('ACTIVITY_LOG_VIEW')")
    public ResponseEntity<List<ActivityLogSideDropViewDto>> viewActivityByEntity(@PathVariable String entity){

        List<ActivityLogSideDropViewDto> activityLogSideDropViewDtoList = activityLogService.viewActivityByEntity(entity);

        return ResponseEntity.status(HttpStatus.OK).body(activityLogSideDropViewDtoList);
    }

    //Activity Log of a specific entity object(eg: PowerLink(Brand))
    @GetMapping("/{entity}/{entityId}")
    @PreAuthorize("hasAuthority('ACTIVITY_LOG_VIEW')")
    public ResponseEntity<List<ActivityLogViewDto>> viewActivityByEntityAndId(@PathVariable String entity, @PathVariable Long entityId){

        List<ActivityLogViewDto> activityLogViewDtoList = activityLogService.viewActivityByEntityAndId(entity, entityId);

        return ResponseEntity.status(HttpStatus.OK).body(activityLogViewDtoList);
    }

    //Activity Log of the whole system
    @GetMapping
    @PreAuthorize("hasAuthority('ACTIVITY_LOG_VIEW')")
    public ResponseEntity<List<ActivityLogTableViewDto>> viewAllActivityLog() {

        List<ActivityLogTableViewDto> activityLogTableViewDtoList = activityLogService.viewAllActivityLog();

        return ResponseEntity.status(HttpStatus.OK).body(activityLogTableViewDtoList);
    }
}
