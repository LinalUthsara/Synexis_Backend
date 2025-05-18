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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid argument: " + ex.getMessage());
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

}
