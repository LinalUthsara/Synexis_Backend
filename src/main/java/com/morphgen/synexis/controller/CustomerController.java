package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.CustomerDto;
import com.morphgen.synexis.dto.CustomerSideDropViewDto;
import com.morphgen.synexis.dto.CustomerTableViewDto;
import com.morphgen.synexis.dto.CustomerViewDto;
import com.morphgen.synexis.entity.File;
import com.morphgen.synexis.enums.DocumentType;
import com.morphgen.synexis.service.CustomerService;

@RestController
@RequestMapping("api/synexis/customer")
@CrossOrigin("*")

public class CustomerController {
    
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<String> createCustomer(@ModelAttribute CustomerDto customerDto) throws IOException {
        
        customerService.createCustomer(customerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Customer successfully created!");
    }

    @GetMapping
    public ResponseEntity<List<CustomerTableViewDto>> viewCustomerTable(){
        
        List<CustomerTableViewDto> customerTableViewDtoList = customerService.viewCustomerTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(customerTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<CustomerSideDropViewDto>> viewCustomerSideDrop(){

        List<CustomerSideDropViewDto> customerSideDropViewDtoList = customerService.viewCustomerSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(customerSideDropViewDtoList);

    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerViewDto> viewCustomerById(@PathVariable Long customerId){

        CustomerViewDto customerViewDto = customerService.viewCustomerById(customerId);

        return ResponseEntity.status(HttpStatus.OK).body(customerViewDto);
    }

    @GetMapping("/{customerId}/{documentType}")
    public ResponseEntity<byte[]> getCustomerDocument(
            @PathVariable Long customerId,
            @PathVariable DocumentType documentType) {

        File file = customerService.getCustomerFile(customerId, documentType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file.getFileData());
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<String> updateCustomer(@PathVariable Long customerId, @ModelAttribute CustomerDto customerDto){
        
        customerService.updateCustomer(customerId, customerDto);

        return ResponseEntity.status(HttpStatus.OK).body("Customer successfully updated!");
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId){

        customerService.deleteCustomer(customerId);

        return ResponseEntity.status(HttpStatus.OK).body("Customer successfully deleted!");
    }

    @PatchMapping("/reactivate/{customerId}")
    public ResponseEntity<String> reactivateCustomer(@PathVariable Long customerId){

        customerService.reactivateCustomer(customerId);

        return ResponseEntity.status(HttpStatus.OK).body("Customer successfully reactivated!");
    }
}
