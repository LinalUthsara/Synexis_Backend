package com.morphgen.synexis.entity;

import java.time.LocalDateTime;

import com.morphgen.synexis.enums.Action;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actLogId;

    private String entity;

    private Long entityId;

    private String entityName;

    @Enumerated(EnumType.STRING)
    private Action actLogAction;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String actLogDetails;

    // private String actLogPerformedBy;

    private LocalDateTime actLogTimestamp;

    @PrePersist
    protected void onCreate(){
        this.actLogTimestamp = LocalDateTime.now();
    }
}
