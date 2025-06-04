package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class EnclosureDto {
    
    private Long enclosureId;

    private Boolean surfaceTypeOutdoor;

    private Boolean surfaceTypeIndoor;

    private Boolean flushTypeOutdoor;

    private Boolean flushTypeIndoor;

    private Boolean freestandingTypeOutdoor;

    private Boolean freestandingTypeIndoor;

    private Boolean lidTypeOutdoor;

    private Boolean lidTypeIndoor;

    private Boolean outdoorWallOutdoor;

    private Boolean outdoorWallIndoor;

    private Boolean feederPillarOutdoor;
    
    private Boolean feederPillarIndoor;

}
