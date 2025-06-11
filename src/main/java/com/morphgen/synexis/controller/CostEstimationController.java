package com.morphgen.synexis.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.dto.CostEstimationTableViewDto;
import com.morphgen.synexis.dto.CostEstimationViewDto;
import com.morphgen.synexis.enums.EstimationStatus;
import com.morphgen.synexis.service.CostEstimationService;

@RestController
@RequestMapping("api/synexis/estimation")
@CrossOrigin("*")

public class CostEstimationController {
    
    private final CostEstimationService costEstimationService;

    public CostEstimationController(CostEstimationService costEstimationService) {
        this.costEstimationService = costEstimationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ESTIMATION_CREATE')")
    public ResponseEntity<String> createEstimation(@RequestBody CostEstimationDto costEstimationDto) throws IOException {
        
        if(costEstimationDto.getInquiryId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Quotation Number is Required!");
        }

        costEstimationService.createEstimation(costEstimationDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Cost Estimation successfully created!");
    }

    @GetMapping("inquiry/{inquiryId}")
    @PreAuthorize("hasAuthority('ESTIMATION_VIEW')")
    public ResponseEntity<CostEstimationTableViewDto> viewEstimationTableByInquiryId(@PathVariable Long inquiryId) {
        
        CostEstimationTableViewDto costEstimationTableViewDto = costEstimationService.viewEstimationTableByInquiryId(inquiryId);
        
        return ResponseEntity.status(HttpStatus.OK).body(costEstimationTableViewDto);
    }

    @GetMapping("/{estimationId}")
    @PreAuthorize("hasAuthority('ESTIMATION_VIEW')")
    public ResponseEntity<CostEstimationViewDto> viewEstimationById(@PathVariable Long estimationId){

        CostEstimationViewDto costEstimationViewDto = costEstimationService.viewEstimationById(estimationId);

        return ResponseEntity.status(HttpStatus.OK).body(costEstimationViewDto);
    }

    @PutMapping("/{estimationId}")
    @PreAuthorize("hasAuthority('ESTIMATION_UPDATE')")
    public ResponseEntity<String> updateEstimation(@PathVariable Long estimationId, @RequestBody CostEstimationDto costEstimationDto) {
        
        costEstimationService.updateEstimation(estimationId, costEstimationDto);

        return ResponseEntity.status(HttpStatus.OK).body("Estimation successfully updated!");
    }

    @GetMapping("approval/{inquiryId}")
    @PreAuthorize("hasAuthority('ESTIMATION_APPROVAL_VIEW')")
    public ResponseEntity<CostEstimationTableViewDto> viewEstimationApprovalTable(@PathVariable Long inquiryId) {
        
        CostEstimationTableViewDto costEstimationTableViewDto = costEstimationService.viewEstimationApprovalTable(inquiryId);
        
        return ResponseEntity.status(HttpStatus.OK).body(costEstimationTableViewDto);
    }

    @PatchMapping("approval/{estimationId}")
    @PreAuthorize("hasAuthority('ESTIMATION_APPROVE')")
    public ResponseEntity<String> handleEstimation(@PathVariable Long estimationId, @RequestParam EstimationStatus estimationStatus){

        costEstimationService.handleEstimation(estimationId, estimationStatus);

        return ResponseEntity.status(HttpStatus.OK).body("Cost Estimation successfully " + estimationStatus + "!");
    }

}
