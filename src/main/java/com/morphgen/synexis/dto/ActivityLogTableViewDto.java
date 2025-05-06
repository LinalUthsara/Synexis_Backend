package com.morphgen.synexis.dto;

import com.morphgen.synexis.enums.Action;

import lombok.Data;

@Data

public class ActivityLogTableViewDto {
    
    private Long actLogId;

    private String actLogTimestamp;

    private String entity;

    private String entityName;

    private Action actLogAction;

    private String actLogDetails;

    private String actLogPerformedBy;

}
