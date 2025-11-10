package com.khouss.UsersMicroservice.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, message, data, HttpStatus.OK.value()), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, message, data, HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    public static ResponseEntity<ApiResponse<Object>> noContent(String message) {
        return new ResponseEntity<>(new ApiResponse<>(true, message, null, HttpStatus.NO_CONTENT.value()), HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<ApiResponse<Object>> error(String message, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(false, message, null, status.value()), status);
    }
}

