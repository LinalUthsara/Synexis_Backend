package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class ActivityLogViewDto {
    
    private String actLogTimestamp;

    private String actLogAction;

    private String actLogPerformedBy;

    private String actLogDetails;
}
