package com.morphgen.synexis.exception;

public class DesignProcessingException extends RuntimeException {
    
    public DesignProcessingException(String message){
        super(message);
    }

    public DesignProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
