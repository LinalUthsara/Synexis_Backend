package com.morphgen.synexis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data

public class Attachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String fileType;

    private Long fileSize;

    @Lob
    private byte[] fileData;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "jobId")
    private Job job;
}
