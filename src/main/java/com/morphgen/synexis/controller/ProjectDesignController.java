package com.morphgen.synexis.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.ProjectDesignAssetViewDto;
import com.morphgen.synexis.dto.ProjectDesignDto;
import com.morphgen.synexis.dto.ProjectDesignTableViewDto;
import com.morphgen.synexis.dto.ProjectDesignUpdateDto;
import com.morphgen.synexis.enums.DesignStatus;
import com.morphgen.synexis.service.ProjectDesignService;

@RestController
@RequestMapping("api/synexis/projectDesign")
@CrossOrigin("*")

public class ProjectDesignController {
    
    private final ProjectDesignService projectDesignService;

    public ProjectDesignController(ProjectDesignService projectDesignService) {
        this.projectDesignService = projectDesignService;
    }

    @PostMapping("/{boqId}")
    // @PreAuthorize("hasAuthority('BRAND_CREATE')")
    public ResponseEntity<String> createProjectDesign(@PathVariable Long boqId, @ModelAttribute ProjectDesignDto projectDesignDto) throws IOException {

        projectDesignService.createProjectDesign(boqId, projectDesignDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Project Design successfully created!");
    }

    @GetMapping("/{boqId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_APPROVAL_VIEW')")
    public ResponseEntity<ProjectDesignTableViewDto> viewProjectDesignTableByBoqId(@PathVariable Long boqId) {
        
        ProjectDesignTableViewDto projectDesignTableViewDto = projectDesignService.viewProjectDesignTableByBoqId(boqId);
        
        return ResponseEntity.status(HttpStatus.OK).body(projectDesignTableViewDto);
    }

    @GetMapping("/design/{projectDesignId}")
    // @PreAuthorize("hasAuthority('BRAND_VIEW')")
    public ResponseEntity<ProjectDesignAssetViewDto> viewProjectDesignByProjectDesignId(@PathVariable Long projectDesignId){

        ProjectDesignAssetViewDto projectDesignAssetViewDto = projectDesignService.viewProjectDesignByProjectDesignId(projectDesignId);

        return ResponseEntity.status(HttpStatus.OK).body(projectDesignAssetViewDto);
    }

    @GetMapping("/viewDesign/{designId}")
    // @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<byte[]> viewProjectDesign(@PathVariable Long designId, @RequestParam(defaultValue = "inline") String disposition) {
        
        return projectDesignService.viewProjectDesign(designId, disposition);
    }

    @GetMapping("/approval/{boqId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_APPROVAL_VIEW')")
    public ResponseEntity<ProjectDesignTableViewDto> viewProjectDesignApprovalTable(@PathVariable Long boqId) {
        
        ProjectDesignTableViewDto projectDesignTableViewDto = projectDesignService.viewProjectDesignApprovalTable(boqId);
        
        return ResponseEntity.status(HttpStatus.OK).body(projectDesignTableViewDto);
    }

    @PutMapping("/{projectDesignId}")
    // @PreAuthorize("hasAuthority('JOB_UPDATE')")
    public ResponseEntity<String> updateProjectDesign(@PathVariable Long projectDesignId, @ModelAttribute ProjectDesignUpdateDto projectDesignUpdateDto){
        
        projectDesignService.updateProjectDesign(projectDesignId, projectDesignUpdateDto);

        return ResponseEntity.status(HttpStatus.OK).body("Project Design successfully updated!");
    }

    @PatchMapping("/approval/design/{projectDesignId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_APPROVE')")
    public ResponseEntity<String> handleEstimation(@PathVariable Long projectDesignId, @RequestParam DesignStatus designStatus){

        projectDesignService.handleProjectDesign(projectDesignId, designStatus);

        return ResponseEntity.status(HttpStatus.OK).body("Project Design successfully " + designStatus + "!");
    }

}
