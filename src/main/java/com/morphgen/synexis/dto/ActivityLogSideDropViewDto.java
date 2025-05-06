package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class ActivityLogSideDropViewDto {
    
    private String entityName;

    private String actLogAction;

    private String actLogPerformedBy;

    private String actLogTimestamp;
}
