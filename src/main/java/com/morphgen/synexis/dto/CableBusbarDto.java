package com.morphgen.synexis.dto;

import lombok.Data;

@Data

public class CableBusbarDto {
    
    private Long cableBusbarId;
    
    private Boolean BusbarIncoming;

    private Boolean BusbarOutgoing;

    private Boolean TopIncoming;

    private Boolean TopOutgoing;

    private Boolean BottomIncoming;

    private Boolean BottomOutgoing;

    private Boolean LeftIncoming;

    private Boolean LeftOutgoing;

    private Boolean RightIncoming;

    private Boolean RightOutgoing;

    private Boolean RearTopIncoming;

    private Boolean RearTopOutgoing;

    private Boolean RearBottomIncoming;
    
    private Boolean RearBottomOutgoing;

}
