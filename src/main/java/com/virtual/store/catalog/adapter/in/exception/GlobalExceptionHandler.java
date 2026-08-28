package com.virtual.store.catalog.adapter.in.exception;

import com.virtual.store.catalog.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String RESOURCE_NOT_FOUND_MESSAGE= "The Resource does not exist";
    private static final String INTERNAL_ERROR_MESSAGE= "Unexpected error";

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex){
        logger.warn("Resource not found: error={}", ex.getMessage());
        return ResponseEntity.badRequest().body(createBody(RESOURCE_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex){
        logger.error("An unexpected error has occurred error={}", ex.getMessage());
        return ResponseEntity.internalServerError().body(createBody(INTERNAL_ERROR_MESSAGE));
    }

    private Map<String, Object> createBody(String message){
        return Map.of(
                "timestamp",Instant.now().toString(),
                "message", message
        );
    }
}
