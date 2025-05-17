package com.morphgen.synexis.exception;

public class EmployeeNotFoundException extends RuntimeException {
    
    public EmployeeNotFoundException(String message){
        super(message);
    }

}
