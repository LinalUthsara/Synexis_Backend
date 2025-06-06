package com.morphgen.synexis.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler {
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException (DataIntegrityViolationException dataIntegrityViolationException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity error occured: " + dataIntegrityViolationException.getMessage());
    }

    @ExceptionHandler(value = InvalidInputException.class)
    public ResponseEntity<String> InvalidInputExceptionHandler (InvalidInputException invalidInputException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid input: " + invalidInputException.getMessage());
    }

    @ExceptionHandler(value = ImageProcessingException.class)
    public ResponseEntity<String> ImageProcessingExceptionHandler (ImageProcessingException imageProcessingException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image Processing Failed: " + imageProcessingException.getMessage());
    }

    @ExceptionHandler(value = BrandNotFoundException.class)
    public ResponseEntity<String> BrandNotFoundExceptionHandler (BrandNotFoundException brandNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + brandNotFoundException.getMessage());
    }

    @ExceptionHandler(value = CategoryNotFoundException.class)
    public ResponseEntity<String> CategoryNotFoundExceptionHandler (CategoryNotFoundException categoryNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + categoryNotFoundException.getMessage());
    }

    @ExceptionHandler(value = UnitNotFoundException.class)
    public ResponseEntity<String> UnitNotFoundExceptionHandler (UnitNotFoundException unitNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + unitNotFoundException.getMessage());
    }

    @ExceptionHandler(value = MaterialNotFoundException.class)
    public ResponseEntity<String> MaterialNotFoundExceptionHandler (MaterialNotFoundException materialNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + materialNotFoundException.getMessage());
    }

    @ExceptionHandler(value = EmployeeNotFoundException.class)
    public ResponseEntity<String> EmployeeNotFoundExceptionHandler (EmployeeNotFoundException employeeNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + employeeNotFoundException.getMessage());
    }

    @ExceptionHandler(value = CustomerNotFoundException.class)
    public ResponseEntity<String> CustomerNotFoundExceptionHandler (CustomerNotFoundException customerNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + customerNotFoundException.getMessage());
    }

    @ExceptionHandler(value = InquiryNotFoundException.class)
    public ResponseEntity<String> InquiryNotFoundExceptionHandler (InquiryNotFoundException inquiryNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + inquiryNotFoundException.getMessage());
    }

    @ExceptionHandler(value = CostEstimationNotFoundException.class)
    public ResponseEntity<String> CostEstimationNotFoundExceptionHandler (CostEstimationNotFoundException costEstimationNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + costEstimationNotFoundException.getMessage());
    }

    @ExceptionHandler(value = InvalidStatusException.class)
    public ResponseEntity<String> InvalidStatusExceptionHandler (InvalidStatusException invalidStatusException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid status: " + invalidStatusException.getMessage());
    }

    @ExceptionHandler(value = IllegalStatusTransitionException.class)
    public ResponseEntity<String> IllegalStatusTransitionExceptionHandler (IllegalStatusTransitionException illegalStatusTransitionException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid status transition: " + illegalStatusTransitionException.getMessage());
    }

    @ExceptionHandler(value = JobNotFoundException.class)
    public ResponseEntity<String> JobNotFoundExceptionHandler (JobNotFoundException jobNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + jobNotFoundException.getMessage());
    }

    @ExceptionHandler(value = AttachmentProcessingException.class)
    public ResponseEntity<String> AttachmentProcessingExceptionHandler (AttachmentProcessingException attachmentProcessingException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attachment Processing Failed: " + attachmentProcessingException.getMessage());
    }

    @ExceptionHandler(value = AttachmentNotFoundException.class)
    public ResponseEntity<String> AttachmentNotFoundExceptionHandler (AttachmentNotFoundException attachmentNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + attachmentNotFoundException.getMessage());
    }

    @ExceptionHandler(value = ImageNotFoundException.class)
    public ResponseEntity<String> ImageNotFoundExceptionHandler (ImageNotFoundException imageNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + imageNotFoundException.getMessage());
    }

    @ExceptionHandler(value = FileProcessingException.class)
    public ResponseEntity<String> FileProcessingExceptionHandler (FileProcessingException fileProcessingException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File Processing Failed: " + fileProcessingException.getMessage());
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<String> FileNotFoundExceptionHandler (FileNotFoundException fileNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("An Unexpected error occured: " + fileNotFoundException.getMessage());
    }

}
