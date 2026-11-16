package com.homeservices.exception;

/** Raised when a requested resource is missing or is not visible to the current caller. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
