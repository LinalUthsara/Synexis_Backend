package com.morphgen.synexis.exception;

public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message){
        super(message);
    }
}
