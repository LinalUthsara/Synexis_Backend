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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.BoqDesignDto;
import com.morphgen.synexis.dto.BoqDto;
import com.morphgen.synexis.dto.BoqTableViewDto;
import com.morphgen.synexis.dto.BoqViewDto;
import com.morphgen.synexis.dto.CustomerDesignAssetViewDto;
import com.morphgen.synexis.service.BillOfQuantitiesService;

@RestController
@RequestMapping("api/synexis/boq")
@CrossOrigin("*")

public class BillOfQuantitiesController {
    
    private final BillOfQuantitiesService billOfQuantitiesService;

    public BillOfQuantitiesController(BillOfQuantitiesService billOfQuantitiesService) {
        this.billOfQuantitiesService = billOfQuantitiesService;
    }

    @PostMapping
    // @PreAuthorize("hasAuthority('ESTIMATION_CREATE')")
    public ResponseEntity<String> createBOQ(@RequestBody BoqDto boqDto) throws IOException {

        billOfQuantitiesService.createBOQ(boqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Bill of Quantities successfully created!");
    }

    @GetMapping("job/{jobId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_VIEW')")
    public ResponseEntity<BoqTableViewDto> viewBoqByJobId(@PathVariable Long jobId) {
        
        BoqTableViewDto boqTableViewDto = billOfQuantitiesService.viewBoqByJobId(jobId);
        
        return ResponseEntity.status(HttpStatus.OK).body(boqTableViewDto);
    }

    @PutMapping("/{boqId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_UPDATE')")
    public ResponseEntity<String> updateBOQ(@PathVariable Long boqId, @RequestBody BoqDto boqDto) {
        
        billOfQuantitiesService.updateBOQ(boqId, boqDto);

        return ResponseEntity.status(HttpStatus.OK).body("Bill of Quantities successfully updated!");
    }

    @GetMapping("/{boqId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_VIEW')")
    public ResponseEntity<BoqViewDto> viewBoqById(@PathVariable Long boqId){

        BoqViewDto boqViewDto = billOfQuantitiesService.viewBoqById(boqId);

        return ResponseEntity.status(HttpStatus.OK).body(boqViewDto);
    }

    @PutMapping("/customerDesign/{boqId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_UPDATE')")
    public ResponseEntity<String> addCustomerDesign(@PathVariable Long boqId, @ModelAttribute BoqDesignDto boqDesignDto) {
        
        billOfQuantitiesService.addCustomerDesign(boqId, boqDesignDto);

        return ResponseEntity.status(HttpStatus.OK).body("Customer Designs successfully updated!");
    }

    @GetMapping("/customerDesign/{boqId}")
    // @PreAuthorize("hasAuthority('BRAND_VIEW')")
    public ResponseEntity<CustomerDesignAssetViewDto> viewCustomerDesignByBoqId(@PathVariable Long boqId){

        CustomerDesignAssetViewDto customerDesignAssetViewDto = billOfQuantitiesService.viewCustomerDesignByBoqId(boqId);

        return ResponseEntity.status(HttpStatus.OK).body(customerDesignAssetViewDto);
    }

    @GetMapping("/viewCustomerDesign/{cDesignId}")
    // @PreAuthorize("hasAuthority('JOB_VIEW')")
    public ResponseEntity<byte[]> viewCustomerDesign(@PathVariable Long cDesignId, @RequestParam(defaultValue = "inline") String disposition) {
        
        return billOfQuantitiesService.viewCustomerDesign(cDesignId, disposition);
    }

    @PatchMapping("handle/{boqId}")
    // @PreAuthorize("hasAuthority('JOB_APPROVE')")
    public ResponseEntity<String> handleBillOfQuantities(@PathVariable Long boqId){

        billOfQuantitiesService.handleBillOfQuantities(boqId);

        return ResponseEntity.status(HttpStatus.OK).body("BOQ successfully submitted!");
    }

    @GetMapping("submittedBOQ/{jobId}")
    // @PreAuthorize("hasAuthority('ESTIMATION_VIEW')")
    public ResponseEntity<BoqTableViewDto> viewSubmittedBoq(@PathVariable Long jobId) {
        
        BoqTableViewDto boqTableViewDto = billOfQuantitiesService.viewSubmittedBoq(jobId);
        
        return ResponseEntity.status(HttpStatus.OK).body(boqTableViewDto);
    }

}

