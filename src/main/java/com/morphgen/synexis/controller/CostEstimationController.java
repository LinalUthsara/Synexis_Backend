package com.morphgen.synexis.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.dto.CostEstimationTableViewDto;
import com.morphgen.synexis.dto.CostEstimationViewDto;
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
    public ResponseEntity<String> createEstimation(@RequestBody CostEstimationDto costEstimationDto) throws IOException {
        
        if(costEstimationDto.getInquiryId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Quotation Number is Required!");
        }

        costEstimationService.createEstimation(costEstimationDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Cost Estimation successfully created!");
    }

    @GetMapping("inquiry/{inquiryId}")
    public ResponseEntity<CostEstimationTableViewDto> viewEstimationTableByInquiryId(@PathVariable Long inquiryId) {
        
        CostEstimationTableViewDto costEstimationTableViewDto = costEstimationService.viewEstimationTableByInquiryId(inquiryId);
        
        return ResponseEntity.status(HttpStatus.OK).body(costEstimationTableViewDto);
    }

    @GetMapping("/{estimationId}")
    public ResponseEntity<CostEstimationViewDto> viewEstimationById(@PathVariable Long estimationId){

        CostEstimationViewDto costEstimationViewDto = costEstimationService.viewEstimationById(estimationId);

        return ResponseEntity.status(HttpStatus.OK).body(costEstimationViewDto);
    }

    @PutMapping("/{estimationId}")
    public ResponseEntity<String> updateEstimation(@PathVariable Long estimationId, @RequestBody CostEstimationDto costEstimationDto) {
        
        costEstimationService.updateEstimation(estimationId, costEstimationDto);

        return ResponseEntity.status(HttpStatus.OK).body("Estimation successfully updated!");
    }

}
