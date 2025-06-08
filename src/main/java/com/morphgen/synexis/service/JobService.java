package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.JobDto;
import com.morphgen.synexis.dto.JobSideDropViewDto;
import com.morphgen.synexis.dto.JobTableViewDto;
import com.morphgen.synexis.dto.JobViewDto;
import com.morphgen.synexis.entity.Job;
import com.morphgen.synexis.enums.JobStatus;

@Service

public interface JobService {
    
    Job createJob(JobDto jobDto);

    ResponseEntity<byte[]> viewAttachment(Long attachmentId);
    List<JobTableViewDto> viewJobTable();
    List<JobSideDropViewDto> viewJobSideDrop();
    JobViewDto viewJobById(Long jobId);

    Job updateJob(Long jobId, JobDto jobDto);
    
    void deleteJob(Long jobId);

    Job handleJob(Long jobId, JobStatus jobStatus);
}
