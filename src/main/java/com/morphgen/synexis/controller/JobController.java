package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.JobCreateDto;
import com.morphgen.synexis.dto.JobDto;
import com.morphgen.synexis.dto.JobSideDropViewDto;
import com.morphgen.synexis.dto.JobTableViewDto;
import com.morphgen.synexis.dto.JobViewDto;
import com.morphgen.synexis.enums.JobStatus;
import com.morphgen.synexis.service.JobService;

@RestController
@RequestMapping("api/synexis/job")
@CrossOrigin("*")

public class JobController {
    
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('JOB_CREATE')")
    public ResponseEntity<String> createJob(@ModelAttribute JobCreateDto jobDto) throws IOException {

        jobService.createJob(jobDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Job successfully created!");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<List<JobTableViewDto>> viewJobTable(){
        
        List<JobTableViewDto> jobTableViewDtoList = jobService.viewJobTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(jobTableViewDtoList);
    }

    @GetMapping("/attachment/{attachmentId}")
    @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<byte[]> viewJobAttachment(@PathVariable Long attachmentId, @RequestParam(defaultValue = "inline") String disposition) {
        
        return jobService.viewAttachment(attachmentId, disposition);
    }

    @GetMapping("/sideDrop")
    @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<List<JobSideDropViewDto>> viewJobSideDrop(){

        List<JobSideDropViewDto> jobSideDropViewDtoList = jobService.viewJobSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(jobSideDropViewDtoList);
    }

    @GetMapping("/{jobId}")
    @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<JobViewDto> viewJobById(@PathVariable Long jobId){

        JobViewDto jobViewDto = jobService.viewJobById(jobId);

        return ResponseEntity.status(HttpStatus.OK).body(jobViewDto);
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public ResponseEntity<String> updateJob(@PathVariable Long jobId, @ModelAttribute JobDto jobDto){
        
        jobService.updateJob(jobId, jobDto);

        return ResponseEntity.status(HttpStatus.OK).body("Job successfully updated!");
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAuthority('JOB_DELETE')")
    public ResponseEntity<String> deleteJob(@PathVariable Long jobId){

        jobService.deleteJob(jobId);

        return ResponseEntity.status(HttpStatus.OK).body("Job successfully deleted!");
    }

    @PatchMapping("approval/{jobId}")
    @PreAuthorize("hasAuthority('JOB_APPROVE')")
    public ResponseEntity<String> handleJob(@PathVariable Long jobId, @RequestParam JobStatus jobStatus){

        jobService.handleJob(jobId, jobStatus);

        return ResponseEntity.status(HttpStatus.OK).body("Job successfully " + jobStatus + "!");
    }

    @GetMapping("/jobForDesign")
    @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<List<JobTableViewDto>> viewJobTableForDesign(){
        
        List<JobTableViewDto> jobTableViewDtoList = jobService.viewJobTableForDesign();
        
        return ResponseEntity.status(HttpStatus.OK).body(jobTableViewDtoList);
    }

}
