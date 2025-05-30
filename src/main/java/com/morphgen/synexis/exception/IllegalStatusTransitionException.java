package com.morphgen.synexis.exception;

public class IllegalStatusTransitionException extends RuntimeException {
    
    public IllegalStatusTransitionException(String message){
        super(message);
    }
}
