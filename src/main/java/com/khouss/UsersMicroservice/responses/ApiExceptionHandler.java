package com.khouss.UsersMicroservice.responses;

import com.khouss.UsersMicroservice.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
        String msg = ex.getMessage() == null ? "Requête invalide" : ex.getMessage();
        return ResponseHandler.error(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        String msg = ex.getMessage() == null ? "Ressource non trouvée" : ex.getMessage();
        return ResponseHandler.error(msg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {

        String msg = ex.getMessage() == null ? "Erreur interne du serveur" : "Erreur interne du serveur";
        return ResponseHandler.error(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
