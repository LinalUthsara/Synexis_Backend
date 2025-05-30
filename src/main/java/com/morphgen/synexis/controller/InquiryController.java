package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.InquiryDto;
import com.morphgen.synexis.dto.InquirySideDropViewDto;
import com.morphgen.synexis.dto.InquiryTableViewDto;
import com.morphgen.synexis.dto.InquiryViewDto;
import com.morphgen.synexis.service.InquiryService;

@RestController
@RequestMapping("api/synexis/inquiry")
@CrossOrigin("*")

public class InquiryController {
    
    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<String> createInquiry(@RequestBody InquiryDto inquiryDto) throws IOException {

        inquiryService.createInquiry(inquiryDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Inquiry successfully created!");
    }

    @GetMapping
    public ResponseEntity<List<InquiryTableViewDto>> viewInquiryTable(){
        
        List<InquiryTableViewDto> inquiryTableViewDtoList = inquiryService.viewInquiryTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(inquiryTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<InquirySideDropViewDto>> viewInquirySideDrop(){

        List<InquirySideDropViewDto> inquirySideDropViewDtoList = inquiryService.viewInquirySideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(inquirySideDropViewDtoList);

    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryViewDto> viewInquiryById(@PathVariable Long inquiryId){

        InquiryViewDto inquiryViewDto = inquiryService.viewInquiryById(inquiryId);

        return ResponseEntity.status(HttpStatus.OK).body(inquiryViewDto);
    }

    @PutMapping("/{inquiryId}")
    public ResponseEntity<String> updateInquiry(@PathVariable Long inquiryId, @RequestBody InquiryDto inquiryDto){
        
        inquiryService.updateInquiry(inquiryId, inquiryDto);

        return ResponseEntity.status(HttpStatus.OK).body("Inquiry successfully updated!");
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<String> deleteInquiry(@PathVariable Long inquiryId){

        inquiryService.deleteInquiry(inquiryId);

        return ResponseEntity.status(HttpStatus.OK).body("Inquiry successfully deleted!");
    }

}