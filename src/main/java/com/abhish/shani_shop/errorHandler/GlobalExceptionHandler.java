package com.abhish.shani_shop.errorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        APIErrorResponse apiErrorResponse = new APIErrorResponse("Username not found: " + e.getMessage(),
                HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatusCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIErrorResponse> handleAuthenticationException(AuthenticationException e) {
        APIErrorResponse apiErrorResponse = new APIErrorResponse("Authentication failed: " + e.getMessage(),
                HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatusCode());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<APIErrorResponse> handleJwtException(JwtException e) {
        APIErrorResponse apiErrorResponse = new APIErrorResponse("Invalid JWT token: " + e.getMessage(),
                HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatusCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        APIErrorResponse apiErrorResponse = new APIErrorResponse(
                "Access denied: Insufficient permission. " + e.getMessage(), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleGeneralException(Exception e) {
        APIErrorResponse apiErrorResponse = new APIErrorResponse("An unexpected error occurred: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatusCode());
    }
}
