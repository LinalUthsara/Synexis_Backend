package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.JobDto;
import com.morphgen.synexis.dto.JobSideDropViewDto;
import com.morphgen.synexis.dto.JobTableViewDto;
import com.morphgen.synexis.dto.JobViewDto;
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
    public ResponseEntity<String> createJob(@ModelAttribute JobDto jobDto) throws IOException {

        jobService.createJob(jobDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Job successfully created!");
    }

    @GetMapping
    public ResponseEntity<List<JobTableViewDto>> viewJobTable(){
        
        List<JobTableViewDto> jobTableViewDtoList = jobService.viewJobTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(jobTableViewDtoList);
    }

    @GetMapping("/attachment/{attachmentId}")
    public ResponseEntity<byte[]> getBrandImage(@PathVariable Long attachmentId) {
        
        return jobService.viewAttachment(attachmentId);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<JobSideDropViewDto>> viewJobSideDrop(){

        List<JobSideDropViewDto> jobSideDropViewDtoList = jobService.viewJobSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(jobSideDropViewDtoList);

    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobViewDto> viewJobById(@PathVariable Long jobId){

        JobViewDto jobViewDto = jobService.viewJobById(jobId);

        return ResponseEntity.status(HttpStatus.OK).body(jobViewDto);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<String> updateJob(@PathVariable Long jobId, @ModelAttribute JobDto jobDto){
        
        jobService.updateJob(jobId, jobDto);

        return ResponseEntity.status(HttpStatus.OK).body("Job successfully updated!");
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<String> deleteJob(@PathVariable Long jobId){

        jobService.deleteJob(jobId);

        return ResponseEntity.status(HttpStatus.OK).body("Job successfully deleted!");
    }

}
