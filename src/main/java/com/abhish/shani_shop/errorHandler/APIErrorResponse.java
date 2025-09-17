package com.abhish.shani_shop.errorHandler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class APIErrorResponse {
    private LocalDateTime timeStamp;
    private String error;
    private HttpStatus statusCode;

    public APIErrorResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    public APIErrorResponse(String error, HttpStatus statusCode) {
        this();
        this.error = error;
        this.statusCode = statusCode;
    }

}
